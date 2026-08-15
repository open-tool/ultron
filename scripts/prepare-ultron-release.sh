#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_REPO="$(cd "$SCRIPT_DIR/.." && pwd)"
VALIDATOR="$DEFAULT_REPO/scripts/release/validate-release.sh"

usage() {
  cat >&2 <<'EOF'
Usage:
  scripts/prepare-ultron-release.sh [--dry-run] [--repo <repo>] <version>

Creates a reviewable release PR. It never publishes Maven artifacts, creates
tags, creates GitHub Releases, or sends Telegram messages.
EOF
}

fail() {
  echo "prepare release failed: $*" >&2
  exit 1
}

require_tool() {
  command -v "$1" >/dev/null 2>&1 || fail "required tool '$1' is not installed"
}

current_version() {
  local repo="$1"
  sed -n 's/^VERSION_NAME=//p' "$repo/gradle.properties" | head -n 1
}

previous_release_ref() {
  local repo="$1"
  local previous
  previous="$(git -C "$repo" describe --tags --abbrev=0 --match '[0-9]*' 2>/dev/null || true)"
  if [[ -n "$previous" ]]; then
    printf '%s' "$previous"
  else
    current_version "$repo"
  fi
}

release_date() {
  date +"%B %e, %Y" | sed 's/  / /g'
}

update_version_file() {
  local repo="$1"
  local version="$2"
  local file="$repo/gradle.properties"
  [[ -f "$file" ]] || fail "missing gradle.properties"
  awk -v version="$version" '
    BEGIN { updated = 0 }
    /^VERSION_NAME=/ {
      print "VERSION_NAME=" version
      updated = 1
      next
    }
    { print }
    END {
      if (!updated) {
        exit 1
      }
    }
  ' "$file" >"$file.tmp" || fail "VERSION_NAME not found in gradle.properties"
  mv "$file.tmp" "$file"
}

REPO_URL="https://github.com/open-tool/ultron"

# Prints "<pr number>\t<title>" for every pull request merged into the range,
# and "\t<subject>" for commits that landed without a pull request.
# The whole range is walked instead of --first-parent, because a pull request
# merged into another branch first (develop) is not on the first-parent chain.
collect_changes() {
  local repo="$1"
  local range="$2"
  shift 2
  git -C "$repo" log --format='%s%x1f%b%x1e' "$@" "$range" | awk '
    BEGIN { RS = "\x1e"; FS = "\x1f" }
    function trim(value) {
      gsub(/^[[:space:]]+|[[:space:]]+$/, "", value)
      return value
    }
    function is_release_chore(title) {
      return title ~ /^Release [0-9]+\.[0-9]+\.[0-9]+$/ ||
             title ~ /^Add reviewed release notes/ ||
             title ~ /^Merge (branch|remote-tracking branch)/
    }
    {
      subject = trim($1)
      body = $2
      if (subject == "") next

      number = ""
      title = subject

      if (match(subject, /^Merge pull request #[0-9]+ /)) {
        number = substr(subject, 21, RLENGTH - 21)
        gsub(/[^0-9]/, "", number)
        # A merge commit carries the pull request title on the first body line.
        split(body, lines, "\n")
        title = ""
        for (i = 1; i in lines; i++) {
          if (trim(lines[i]) != "") {
            title = trim(lines[i])
            break
          }
        }
        if (title == "") title = subject
      } else if (match(subject, /\(#[0-9]+\)$/)) {
        number = substr(subject, RSTART + 2, RLENGTH - 3)
        title = trim(substr(subject, 1, RSTART - 1))
      }

      if (is_release_chore(title)) next
      if (number != "") {
        if (number in seen) next
        seen[number] = 1
      }
      print number "\t" title
    }
  '
}

format_highlights() {
  local repo="$1"
  local previous="$2"
  local range="$previous..HEAD"
  git -C "$repo" rev-parse -q --verify "$previous^{commit}" >/dev/null 2>&1 || return 0
  collect_changes "$repo" "$range" | awk -v repo_url="$REPO_URL" '
    BEGIN { FS = "\t" }
    $1 == "" { next }
    {
      title = $2
      if (title !~ /[.!?]$/) title = title "."
      print "- " title " [#" $1 "](" repo_url "/pull/" $1 ")"
    }
  '
}

# Only the first-parent chain is walked here: commits that belong to a merged
# pull request are already covered by its highlight.
unlinked_changes() {
  local repo="$1"
  local previous="$2"
  git -C "$repo" rev-parse -q --verify "$previous^{commit}" >/dev/null 2>&1 || return 0
  collect_changes "$repo" "$previous..HEAD" --first-parent --no-merges |
    awk -F '\t' '$1 == "" { print "- " $2 }'
}

insert_release_notes() {
  local repo="$1"
  local version="$2"
  local previous="$3"
  # History is read from the real checkout, which differs from the target
  # directory during a dry run.
  local git_repo="${4:-$repo}"
  local file="$repo/docs/docs/release-notes.md"
  local date_text
  date_text="$(release_date)"
  [[ -f "$file" ]] || fail "missing docs/docs/release-notes.md"
  if grep -qx "## Version $version" "$file"; then
    fail "release notes already contain version $version"
  fi

  RELEASE_HIGHLIGHTS="$(format_highlights "$git_repo" "$previous")"
  if [[ -z "$RELEASE_HIGHLIGHTS" ]]; then
    RELEASE_HIGHLIGHTS="- TODO: Replace with reviewed release highlight."
  fi
  export RELEASE_HIGHLIGHTS

  awk -v version="$version" -v previous="$previous" -v date_text="$date_text" '
    function print_section() {
      print "## Version " version
      print ""
      print "_Released " date_text "_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/" version ") · [Full changelog](https://github.com/open-tool/ultron/compare/" previous "..." version ")"
      print ""
      count = split(ENVIRON["RELEASE_HIGHLIGHTS"], highlights, "\n")
      for (i = 1; i <= count; i++) {
        if (highlights[i] != "") print highlights[i]
      }
    }
    /^## Version / && !inserted {
      print_section()
      print ""
      inserted = 1
    }
    { print }
    END {
      if (!inserted) {
        print ""
        print_section()
      }
    }
  ' "$file" >"$file.tmp"
  mv "$file.tmp" "$file"
  unset RELEASE_HIGHLIGHTS
}

write_pr_body() {
  local repo="$1"
  local version="$2"
  local previous="$3"
  local output="$4"
  cat >"$output" <<EOF
## Release $version

Merging this PR authorizes automated publication for Ultron $version.

After merge, CI will:
- create tag \`$version\` on the merge commit
- publish Ultron artifacts to Maven Central
- create the GitHub Release
- send the Telegram announcement

### Reviewed release notes

Highlights in \`docs/docs/release-notes.md\` are drafted from the pull requests merged
since $previous. Rewrite them into user-facing wording before merging: the same text is
published to the docs site, the GitHub Release body, and the Telegram announcement.

### Changelog

https://github.com/open-tool/ultron/compare/$previous...$version

### Checklist

- [ ] \`VERSION_NAME\` is \`$version\`
- [ ] \`docs/docs/release-notes.md\` contains reviewed highlights
- [ ] Release guard workflow is green
- [ ] Merge approval is intentional; no separate publish approval will be requested
EOF
}

assert_master_ready() {
  local repo="$1"
  local branch
  branch="$(git -C "$repo" rev-parse --abbrev-ref HEAD)"
  [[ "$branch" == "master" ]] || fail "run from master, current branch is '$branch'"
  git -C "$repo" rev-parse --verify origin/master >/dev/null 2>&1 || fail "origin/master is missing; fetch origin first"
  git -C "$repo" merge-base --is-ancestor origin/master HEAD || fail "local master does not contain origin/master"
}

assert_release_branch_absent() {
  local repo="$1"
  local branch="$2"
  if git -C "$repo" show-ref --verify --quiet "refs/heads/$branch"; then
    fail "local branch '$branch' already exists"
  fi
  if git -C "$repo" remote get-url origin >/dev/null 2>&1 && git -C "$repo" ls-remote --exit-code --heads origin "$branch" >/dev/null 2>&1; then
    fail "remote branch '$branch' already exists"
  fi
}

print_diff() {
  local original="$1"
  local generated="$2"
  if command -v diff >/dev/null 2>&1; then
    diff -u "$original" "$generated" || true
  else
    echo "diff tool not found; generated file: $generated"
  fi
}

# Commits that landed without a pull request are reported to the operator
# instead of the notes file, so the drafted highlights stay reviewable.
print_unlinked_changes() {
  local repo="$1"
  local previous="$2"
  local unlinked
  unlinked="$(unlinked_changes "$repo" "$previous")"
  [[ -n "$unlinked" ]] || return 0
  echo
  echo "Commits without a pull request reference (add manually if user-facing):"
  printf '%s\n' "$unlinked"
}

dry_run() {
  local repo="$1"
  local version="$2"
  local previous="$3"
  local tmp
  tmp="$(mktemp -d)"
  mkdir -p "$tmp/docs/docs"
  cp "$repo/gradle.properties" "$tmp/gradle.properties"
  cp "$repo/docs/docs/release-notes.md" "$tmp/docs/docs/release-notes.md"
  update_version_file "$tmp" "$version"
  insert_release_notes "$tmp" "$version" "$previous" "$repo"
  write_pr_body "$repo" "$version" "$previous" "$tmp/pr-body.md"

  echo "Release $version dry run"
  echo
  echo "Would create branch: release/$version"
  echo "Would update files:"
  echo "- gradle.properties"
  echo "- docs/docs/release-notes.md"
  echo
  print_diff "$repo/gradle.properties" "$tmp/gradle.properties"
  print_diff "$repo/docs/docs/release-notes.md" "$tmp/docs/docs/release-notes.md"
  print_unlinked_changes "$repo" "$previous"
  echo
  echo "PR body:"
  cat "$tmp/pr-body.md"
  rm -rf "$tmp"
}

repo="$DEFAULT_REPO"
dry_run_mode=0
version=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --repo)
      repo="$2"
      shift 2
      ;;
    --dry-run)
      dry_run_mode=1
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    -*)
      usage
      exit 2
      ;;
    *)
      version="$1"
      shift
      ;;
  esac
done

[[ -n "$version" ]] || { usage; exit 2; }
repo="$(cd "$repo" && pwd)"

require_tool git
if [[ "$dry_run_mode" != "1" ]]; then
  require_tool gh
fi

"$VALIDATOR" version --version "$version" >/dev/null
"$VALIDATOR" local --repo "$repo" --version "$version" >/dev/null
assert_master_ready "$repo"
release_branch="release/$version"
assert_release_branch_absent "$repo" "$release_branch"
previous="$(previous_release_ref "$repo")"

if [[ "$dry_run_mode" == "1" ]]; then
  dry_run "$repo" "$version" "$previous"
  exit 0
fi

git -C "$repo" checkout -b "$release_branch"
update_version_file "$repo" "$version"
insert_release_notes "$repo" "$version" "$previous"

changed_files="$(mktemp)"
printf '%s\n' gradle.properties docs/docs/release-notes.md >"$changed_files"
"$VALIDATOR" release-pr --repo "$repo" --version "$version" --branch "$release_branch" --pr-title "Release $version" --changed-files "$changed_files" >/dev/null
rm -f "$changed_files"

git -C "$repo" add gradle.properties docs/docs/release-notes.md
git -C "$repo" commit -m "Release $version"
git -C "$repo" push -u origin "$release_branch"

body_file="$(mktemp)"
write_pr_body "$repo" "$version" "$previous" "$body_file"
gh pr create \
  --repo open-tool/ultron \
  --base master \
  --head "$release_branch" \
  --title "Release $version" \
  --body-file "$body_file"
print_unlinked_changes "$repo" "$previous"
rm -f "$body_file"

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

insert_release_notes() {
  local repo="$1"
  local version="$2"
  local previous="$3"
  local file="$repo/docs/docs/release-notes.md"
  local date_text
  date_text="$(release_date)"
  [[ -f "$file" ]] || fail "missing docs/docs/release-notes.md"
  if grep -qx "## Version $version" "$file"; then
    fail "release notes already contain version $version"
  fi

  awk -v version="$version" -v previous="$previous" -v date_text="$date_text" '
    /^## Version / && !inserted {
      print "## Version " version
      print ""
      print "_Released " date_text "_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/" version ") · [Full changelog](https://github.com/open-tool/ultron/compare/" previous "..." version ")"
      print ""
      print "- TODO: Replace with reviewed release highlight."
      print ""
      inserted = 1
    }
    { print }
    END {
      if (!inserted) {
        print ""
        print "## Version " version
        print ""
        print "_Released " date_text "_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/" version ") · [Full changelog](https://github.com/open-tool/ultron/compare/" previous "..." version ")"
        print ""
        print "- TODO: Replace with reviewed release highlight."
      }
    }
  ' "$file" >"$file.tmp"
  mv "$file.tmp" "$file"
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

Please replace every TODO highlight in \`docs/docs/release-notes.md\` before merging.

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
  insert_release_notes "$tmp" "$version" "$previous"
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
rm -f "$body_file"

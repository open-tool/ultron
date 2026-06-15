#!/usr/bin/env bash
set -euo pipefail

VERSION_RE='^[0-9]+\.[0-9]+\.[0-9]+(-[0-9A-Za-z][0-9A-Za-z.-]*)?$'
ALLOWED_RELEASE_FILES=(
  "gradle.properties"
  "docs/docs/release-notes.md"
)

usage() {
  cat >&2 <<'EOF'
Usage:
  validate-release.sh version --version <version>
  validate-release.sh local --repo <repo> --version <version>
  validate-release.sh release-pr --repo <repo> --version <version> --branch <release/version> --pr-title <title> --changed-files <file>
  validate-release.sh tag --repo <repo> --version <version> --tag <tag> --base <base-ref>
EOF
}

fail() {
  echo "release validation failed: $*" >&2
  exit 1
}

require_value() {
  local name="$1"
  local value="$2"
  [[ -n "$value" ]] || fail "missing $name"
}

is_semver() {
  [[ "$1" =~ $VERSION_RE ]]
}

validate_version() {
  local version="$1"
  is_semver "$version" || fail "invalid release version '$version'"
}

repo_file() {
  local repo="$1"
  local path="$2"
  printf '%s/%s' "$repo" "$path"
}

current_version() {
  local repo="$1"
  local file
  file="$(repo_file "$repo" "gradle.properties")"
  [[ -f "$file" ]] || fail "missing gradle.properties"
  sed -n 's/^VERSION_NAME=//p' "$file" | head -n 1
}

assert_version_file() {
  local repo="$1"
  local version="$2"
  local found
  found="$(current_version "$repo")"
  [[ "$found" == "$version" ]] || fail "VERSION_NAME is '$found', expected '$version'"
}

assert_release_notes() {
  local repo="$1"
  local version="$2"
  local file
  file="$(repo_file "$repo" "docs/docs/release-notes.md")"
  [[ -f "$file" ]] || fail "missing docs/docs/release-notes.md"
  grep -qx "## Version $version" "$file" || fail "missing release notes heading for $version"
}

assert_branch_matches() {
  local branch="$1"
  local version="$2"
  [[ "$branch" == "release/$version" ]] || fail "branch '$branch' does not match release/$version"
}

assert_title_matches() {
  local title="$1"
  local version="$2"
  [[ -z "$title" || "$title" == *"$version"* ]] || fail "PR title '$title' does not mention $version"
}

remote_has_tag() {
  local repo="$1"
  local version="$2"
  git -C "$repo" remote get-url origin >/dev/null 2>&1 || return 1
  git -C "$repo" ls-remote --exit-code --tags origin "refs/tags/$version" >/dev/null 2>&1
}

assert_tag_absent() {
  local repo="$1"
  local version="$2"
  if git -C "$repo" rev-parse -q --verify "refs/tags/$version" >/dev/null; then
    fail "tag '$version' already exists locally"
  fi
  if remote_has_tag "$repo" "$version"; then
    fail "tag '$version' already exists on origin"
  fi
}

assert_clean_worktree() {
  local repo="$1"
  [[ -z "$(git -C "$repo" status --porcelain)" ]] || fail "worktree is dirty"
}

assert_allowed_changed_files() {
  local changed_files="$1"
  [[ -f "$changed_files" ]] || fail "changed files list '$changed_files' does not exist"

  local has_version_file=0
  local has_release_notes=0
  local file
  while IFS= read -r file; do
    [[ -z "$file" ]] && continue
    case "$file" in
      gradle.properties)
        has_version_file=1
        ;;
      docs/docs/release-notes.md)
        has_release_notes=1
        ;;
      *)
        fail "release PR contains unexpected file '$file'"
        ;;
    esac
  done <"$changed_files"

  [[ "$has_version_file" == "1" ]] || fail "release PR must change gradle.properties"
  [[ "$has_release_notes" == "1" ]] || fail "release PR must change docs/docs/release-notes.md"
}

assert_tag_reachable_from_base() {
  local repo="$1"
  local tag="$2"
  local base="$3"
  git -C "$repo" rev-parse --verify "refs/tags/$tag" >/dev/null 2>&1 || fail "tag '$tag' does not exist"
  git -C "$repo" rev-parse --verify "$base" >/dev/null 2>&1 || fail "base ref '$base' does not exist"
  git -C "$repo" merge-base --is-ancestor "$tag" "$base" || fail "tag '$tag' is not reachable from $base"
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

mode="${1:-}"
[[ -n "$mode" ]] || { usage; exit 2; }
shift

repo="."
version=""
branch=""
pr_title=""
changed_files=""
tag=""
base="origin/master"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --repo)
      repo="$2"
      shift 2
      ;;
    --version)
      version="$2"
      shift 2
      ;;
    --branch)
      branch="$2"
      shift 2
      ;;
    --pr-title)
      pr_title="$2"
      shift 2
      ;;
    --changed-files)
      changed_files="$2"
      shift 2
      ;;
    --tag)
      tag="$2"
      shift 2
      ;;
    --base)
      base="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      usage
      exit 2
      ;;
  esac
done

repo="$(cd "$repo" && pwd)"
require_value "--version" "$version"
validate_version "$version"

case "$mode" in
  version)
    ;;
  local)
    assert_clean_worktree "$repo"
    assert_tag_absent "$repo" "$version"
    ;;
  release-pr)
    require_value "--branch" "$branch"
    require_value "--changed-files" "$changed_files"
    assert_branch_matches "$branch" "$version"
    assert_title_matches "$pr_title" "$version"
    assert_version_file "$repo" "$version"
    assert_release_notes "$repo" "$version"
    assert_tag_absent "$repo" "$version"
    assert_allowed_changed_files "$changed_files"
    ;;
  tag)
    require_value "--tag" "$tag"
    [[ "$tag" == "$version" ]] || fail "tag '$tag' does not match version '$version'"
    assert_version_file "$repo" "$version"
    assert_release_notes "$repo" "$version"
    assert_tag_reachable_from_base "$repo" "$tag" "$base"
    ;;
  *)
    usage
    exit 2
    ;;
esac

echo "release validation passed: $mode $version"

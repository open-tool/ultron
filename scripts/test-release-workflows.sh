#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORKFLOWS_DIR="$ROOT_DIR/.github/workflows"

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

assert_file() {
  [[ -f "$1" ]] || fail "missing file: $1"
}

assert_contains() {
  local file="$1"
  local pattern="$2"
  grep -qE "$pattern" "$file" || fail "$file does not contain pattern: $pattern"
}

assert_not_contains() {
  local file="$1"
  local pattern="$2"
  if grep -qE "$pattern" "$file"; then
    fail "$file unexpectedly contains pattern: $pattern"
  fi
}

publish="$WORKFLOWS_DIR/maven_central_publish.yml"
guard="$WORKFLOWS_DIR/release_pr_guard.yml"
tagger="$WORKFLOWS_DIR/release_auto_tag.yml"
release_config="$ROOT_DIR/.github/release.yml"
private_release_doc="$ROOT_DIR/RELEASE_PROCESS.md"
public_release_doc="$ROOT_DIR/docs/docs/release-process.md"

assert_file "$publish"
assert_file "$guard"
assert_file "$tagger"
assert_file "$release_config"
assert_file "$private_release_doc"
if [[ -f "$public_release_doc" ]]; then
  fail "release process docs must not be published in Docusaurus docs"
fi

assert_not_contains "$publish" "branches:"
assert_not_contains "$publish" "release/\\*"
assert_contains "$publish" "tags:"
assert_not_contains "$publish" "workflow_dispatch:"
assert_contains "$publish" "repository_dispatch:"
assert_contains "$publish" "ultron-release-tag-created"
assert_contains "$publish" "client_payload\\.version"
assert_contains "$publish" "publishAndReleaseToMavenCentral"
assert_contains "$publish" "validate-release\\.sh tag"
assert_contains "$publish" "gh release create"
assert_contains "$publish" "api\\.telegram\\.org"
assert_contains "$publish" "concurrency:"

assert_contains "$guard" "pull_request:"
assert_contains "$guard" "release/\\*"
assert_contains "$guard" "validate-release\\.sh release-pr"
assert_contains "$guard" "Non-release PR"

assert_contains "$tagger" "pull_request:"
assert_contains "$tagger" "types: \\[closed\\]"
assert_contains "$tagger" "contents: write"
assert_contains "$tagger" "validate-release\\.sh release-pr"
assert_contains "$tagger" "git tag"
assert_contains "$tagger" "git push origin"
assert_contains "$tagger" "gh api"
assert_contains "$tagger" "ultron-release-tag-created"

assert_not_contains "$release_config" "Semver-Major"
assert_not_contains "$release_config" "Semver-Minor"

assert_output_contains() {
  local label="$1"
  local pattern="$2"
  local output="$3"
  grep -q -- "$pattern" <<<"$output" || fail "$label output does not contain: $pattern"
}

assert_output_not_contains() {
  local label="$1"
  local pattern="$2"
  local output="$3"
  if grep -q -- "$pattern" <<<"$output"; then
    fail "$label output unexpectedly contains: $pattern"
  fi
}

SANDBOX=""
cleanup_sandbox() {
  [[ -n "$SANDBOX" ]] && rm -rf "$SANDBOX"
}
trap cleanup_sandbox EXIT

# Builds a repository whose pull request merge is not on the first-parent chain
# of master, reproducing a pull request merged into an integration branch first.
build_sandbox_repo() {
  SANDBOX="$(mktemp -d)"
  WORK_REPO="$SANDBOX/work"
  local origin="$SANDBOX/origin.git"
  local work="$WORK_REPO"

  git init --quiet --bare "$origin"
  git init --quiet -b master "$work"
  git -C "$work" config user.email "release-test@example.com"
  git -C "$work" config user.name "Release Test"

  mkdir -p "$work/docs/docs"
  printf 'GROUP=com.atiurin\nVERSION_NAME=1.0.0\n' >"$work/gradle.properties"
  printf '# Release notes\n\n## Version 1.0.0\n\n- Initial release.\n' >"$work/docs/docs/release-notes.md"
  git -C "$work" add -A
  git -C "$work" commit --quiet -m "Base commit"
  git -C "$work" tag 1.0.0

  git -C "$work" checkout --quiet -b feature
  printf 'feature\n' >"$work/feature.txt"
  git -C "$work" add -A
  git -C "$work" commit --quiet -m "Internal commit inside the pull request"

  git -C "$work" checkout --quiet -b integration master
  git -C "$work" merge --quiet --no-ff feature \
    -m "Merge pull request #42 from contributor/feature" -m "Retry allowed config"

  git -C "$work" checkout --quiet master
  git -C "$work" merge --quiet --no-ff integration -m "Merge remote-tracking branch 'origin/integration'"
  printf 'chore\n' >"$work/chore.txt"
  git -C "$work" add -A
  git -C "$work" commit --quiet -m "Update .gitignore"

  git -C "$work" remote add origin "$origin"
  git -C "$work" push --quiet -u origin master
}

build_sandbox_repo
work_repo="$WORK_REPO"
dry_run_output="$("$ROOT_DIR/scripts/prepare-ultron-release.sh" --dry-run --repo "$work_repo" 2.0.0)"

# A pull request merged through an integration branch must still be drafted.
assert_output_contains "dry run" "- Retry allowed config. \[#42\]" "$dry_run_output"
assert_output_not_contains "dry run" "TODO" "$dry_run_output"
assert_output_not_contains "dry run" "Merge remote-tracking branch" "$dry_run_output"
# Commits belonging to the pull request are covered by its highlight.
assert_output_not_contains "dry run" "Internal commit inside the pull request" "$dry_run_output"
assert_output_contains "dry run" "Commits without a pull request reference" "$dry_run_output"
assert_output_contains "dry run" "- Update .gitignore" "$dry_run_output"

# Unreviewed placeholders must not reach the docs site, GitHub or Telegram.
printf '# Release notes\n\n## Version 2.0.0\n\n- TODO: Replace with reviewed release highlight.\n' \
  >"$work_repo/docs/docs/release-notes.md"
printf 'GROUP=com.atiurin\nVERSION_NAME=2.0.0\n' >"$work_repo/gradle.properties"
changed_files="$SANDBOX/changed-files.txt"
printf '%s\n' gradle.properties docs/docs/release-notes.md >"$changed_files"
if "$ROOT_DIR/scripts/release/validate-release.sh" release-pr --repo "$work_repo" --version 2.0.0 \
  --branch release/2.0.0 --pr-title "Release 2.0.0" --changed-files "$changed_files" >/dev/null 2>&1; then
  fail "validate-release.sh accepted a TODO highlight"
fi

# Telegram receives HTML, so inline Markdown has to be converted.
printf '# Release notes\n\n## Version 2.0.0\n\n- Fixed `setText` handling. [#42](https://github.com/open-tool/ultron/pull/42)\n' \
  >"$work_repo/docs/docs/release-notes.md"
telegram_output="$("$ROOT_DIR/scripts/release/render-release-content.sh" --repo "$work_repo" --version 2.0.0 --format telegram-html)"
assert_output_contains "telegram render" '<code>setText</code>' "$telegram_output"
assert_output_contains "telegram render" '<a href="https://github.com/open-tool/ultron/pull/42">#42</a>' "$telegram_output"
assert_output_not_contains "telegram render" '\[#42\]' "$telegram_output"

echo "release workflow tests passed"

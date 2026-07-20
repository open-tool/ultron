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
  rg -q "$pattern" "$file" || fail "$file does not contain pattern: $pattern"
}

assert_not_contains() {
  local file="$1"
  local pattern="$2"
  if rg -q "$pattern" "$file"; then
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

echo "release workflow tests passed"

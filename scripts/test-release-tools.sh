#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VALIDATOR="$ROOT_DIR/scripts/release/validate-release.sh"
RENDERER="$ROOT_DIR/scripts/release/render-release-content.sh"
PREPARE="$ROOT_DIR/scripts/prepare-ultron-release.sh"

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

assert_success() {
  local description="$1"
  shift
  if ! "$@" >/tmp/ultron-release-test.out 2>/tmp/ultron-release-test.err; then
    echo "STDOUT:" >&2
    cat /tmp/ultron-release-test.out >&2 || true
    echo "STDERR:" >&2
    cat /tmp/ultron-release-test.err >&2 || true
    fail "$description"
  fi
}

assert_failure() {
  local description="$1"
  shift
  if "$@" >/tmp/ultron-release-test.out 2>/tmp/ultron-release-test.err; then
    echo "STDOUT:" >&2
    cat /tmp/ultron-release-test.out >&2 || true
    fail "$description"
  fi
}

setup_repo() {
  local repo="$1"
  mkdir -p "$repo/docs/docs"
  git -C "$repo" init -b master >/dev/null
  git -C "$repo" config user.email "release-test@example.com"
  git -C "$repo" config user.name "Release Test"
  cat >"$repo/gradle.properties" <<'EOF'
GROUP=com.atiurin
POM_ARTIFACT_ID=ultron
VERSION_NAME=2.6.2
EOF
  cat >"$repo/docs/docs/release-notes.md" <<'EOF'
---
sidebar_position: 2
title: Release notes
---

# Release notes

## Version 2.6.2

_Released October 29, 2025_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/2.6.2) · [Full changelog](https://github.com/open-tool/ultron/compare/2.6.1...2.6.2)

- Previous release.
EOF
  git -C "$repo" add .
  git -C "$repo" commit -m "baseline" >/dev/null
  git -C "$repo" branch origin/master
}

prepare_release_files() {
  local repo="$1"
  local version="$2"
  sed -i.bak "s/^VERSION_NAME=.*/VERSION_NAME=$version/" "$repo/gradle.properties"
  rm "$repo/gradle.properties.bak"
  awk -v version="$version" '
    /^## Version / && !inserted {
      print "## Version " version
      print ""
      print "_Released June 15, 2026_ · [GitHub release](https://github.com/open-tool/ultron/releases/tag/" version ") · [Full changelog](https://github.com/open-tool/ultron/compare/2.6.2..." version ")"
      print ""
      print "- Added release automation."
      print ""
      inserted=1
    }
    { print }
  ' "$repo/docs/docs/release-notes.md" >"$repo/docs/docs/release-notes.md.tmp"
  mv "$repo/docs/docs/release-notes.md.tmp" "$repo/docs/docs/release-notes.md"
}

with_tmp_repo() {
  local tmp
  tmp="$(mktemp -d)"
  setup_repo "$tmp/repo"
  set +e
  "$@" "$tmp/repo"
  local status=$?
  set -e
  rm -rf "$tmp"
  return "$status"
}

test_validate_version_accepts_stable_and_prerelease() {
  assert_success "stable semver should be accepted" "$VALIDATOR" version --version 2.6.3
  assert_success "prerelease semver should be accepted" "$VALIDATOR" version --version 2.7.0-alpha01
  assert_failure "invalid semver should be rejected" "$VALIDATOR" version --version 2.7
}

test_release_pr_validation_accepts_valid_release() {
  with_tmp_repo bash -c '
    repo="$1"
    git -C "$repo" checkout -b release/2.6.3 >/dev/null
    prepare_release_files "$repo" 2.6.3
    printf "%s\n" gradle.properties docs/docs/release-notes.md >"$repo/changed-files.txt"
    assert_success "valid release PR should pass" "$VALIDATOR" release-pr --repo "$repo" --version 2.6.3 --branch release/2.6.3 --pr-title "Release 2.6.3" --changed-files "$repo/changed-files.txt"
  ' _
}

test_release_pr_validation_rejects_mismatch() {
  with_tmp_repo bash -c '
    repo="$1"
    git -C "$repo" checkout -b release/2.6.4 >/dev/null
    prepare_release_files "$repo" 2.6.3
    printf "%s\n" gradle.properties docs/docs/release-notes.md >"$repo/changed-files.txt"
    assert_failure "mismatched branch/version should fail" "$VALIDATOR" release-pr --repo "$repo" --version 2.6.3 --branch release/2.6.4 --pr-title "Release 2.6.3" --changed-files "$repo/changed-files.txt"
  ' _
}

test_release_pr_validation_rejects_existing_tag() {
  with_tmp_repo bash -c '
    repo="$1"
    git -C "$repo" tag 2.6.3
    git -C "$repo" checkout -b release/2.6.3 >/dev/null
    prepare_release_files "$repo" 2.6.3
    printf "%s\n" gradle.properties docs/docs/release-notes.md >"$repo/changed-files.txt"
    assert_failure "existing tag should fail" "$VALIDATOR" release-pr --repo "$repo" --version 2.6.3 --branch release/2.6.3 --pr-title "Release 2.6.3" --changed-files "$repo/changed-files.txt"
  ' _
}

test_release_pr_validation_rejects_missing_notes() {
  with_tmp_repo bash -c '
    repo="$1"
    git -C "$repo" checkout -b release/2.6.3 >/dev/null
    sed -i.bak "s/^VERSION_NAME=.*/VERSION_NAME=2.6.3/" "$repo/gradle.properties"
    rm "$repo/gradle.properties.bak"
    printf "%s\n" gradle.properties docs/docs/release-notes.md >"$repo/changed-files.txt"
    assert_failure "missing release notes should fail" "$VALIDATOR" release-pr --repo "$repo" --version 2.6.3 --branch release/2.6.3 --pr-title "Release 2.6.3" --changed-files "$repo/changed-files.txt"
  ' _
}

test_release_pr_validation_rejects_unexpected_files() {
  with_tmp_repo bash -c '
    repo="$1"
    git -C "$repo" checkout -b release/2.6.3 >/dev/null
    prepare_release_files "$repo" 2.6.3
    mkdir -p "$repo/src"
    touch "$repo/src/Unexpected.kt"
    printf "%s\n" gradle.properties docs/docs/release-notes.md src/Unexpected.kt >"$repo/changed-files.txt"
    assert_failure "unexpected file should fail" "$VALIDATOR" release-pr --repo "$repo" --version 2.6.3 --branch release/2.6.3 --pr-title "Release 2.6.3" --changed-files "$repo/changed-files.txt"
  ' _
}

test_local_validation_rejects_dirty_worktree() {
  with_tmp_repo bash -c '
    repo="$1"
    echo dirty >>"$repo/gradle.properties"
    assert_failure "dirty worktree should fail" "$VALIDATOR" local --repo "$repo" --version 2.6.3
  ' _
}

test_tag_validation_rejects_tag_outside_master() {
  with_tmp_repo bash -c '
    repo="$1"
    git -C "$repo" checkout -b release/2.6.3 >/dev/null
    prepare_release_files "$repo" 2.6.3
    git -C "$repo" add .
    git -C "$repo" commit -m "release 2.6.3" >/dev/null
    git -C "$repo" tag 2.6.3
    assert_failure "tag outside origin/master should fail" "$VALIDATOR" tag --repo "$repo" --version 2.6.3 --tag 2.6.3 --base origin/master
  ' _
}

test_render_release_content() {
  with_tmp_repo bash -c '
    repo="$1"
    prepare_release_files "$repo" 2.6.3
    assert_success "github release notes should render" "$RENDERER" --repo "$repo" --version 2.6.3 --format github
    "$RENDERER" --repo "$repo" --version 2.6.3 --format telegram-html >/tmp/ultron-release-test.out
    grep -q "<b>Ultron 2.6.3</b>" /tmp/ultron-release-test.out
    grep -q "ultron-compose" /tmp/ultron-release-test.out
    ! grep -q "ultron-common" /tmp/ultron-release-test.out
  ' _
}

test_prepare_release_dry_run_has_no_side_effects() {
  with_tmp_repo bash -c '
    repo="$1"
    assert_success "prepare dry-run should succeed" "$PREPARE" --repo "$repo" --dry-run 2.6.3
    grep -q "Release 2.6.3" /tmp/ultron-release-test.out
    grep -q "VERSION_NAME=2.6.2" "$repo/gradle.properties"
    ! git -C "$repo" rev-parse -q --verify refs/tags/2.6.3 >/dev/null
  ' _
}

test_prepare_release_dry_run_rejects_dirty_worktree() {
  with_tmp_repo bash -c '
    repo="$1"
    echo dirty >>"$repo/gradle.properties"
    assert_failure "prepare dry-run should reject dirty worktree" "$PREPARE" --repo "$repo" --dry-run 2.6.3
  ' _
}

export -f fail assert_success assert_failure setup_repo prepare_release_files with_tmp_repo
export VALIDATOR RENDERER PREPARE

test_validate_version_accepts_stable_and_prerelease
test_release_pr_validation_accepts_valid_release
test_release_pr_validation_rejects_mismatch
test_release_pr_validation_rejects_existing_tag
test_release_pr_validation_rejects_missing_notes
test_release_pr_validation_rejects_unexpected_files
test_local_validation_rejects_dirty_worktree
test_tag_validation_rejects_tag_outside_master
test_render_release_content
test_prepare_release_dry_run_has_no_side_effects
test_prepare_release_dry_run_rejects_dirty_worktree

echo "release tooling tests passed"

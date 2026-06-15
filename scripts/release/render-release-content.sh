#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat >&2 <<'EOF'
Usage:
  render-release-content.sh --repo <repo> --version <version> --format <github|telegram-html>
EOF
}

fail() {
  echo "release content rendering failed: $*" >&2
  exit 1
}

html_escape() {
  sed \
    -e 's/&/\&amp;/g' \
    -e 's/</\&lt;/g' \
    -e 's/>/\&gt;/g'
}

extract_section() {
  local file="$1"
  local version="$2"
  awk -v version="$version" '
    $0 == "## Version " version {
      capture = 1
    }
    capture && /^## Version / && $0 != "## Version " version {
      exit
    }
    capture {
      print
    }
  ' "$file"
}

extract_highlights() {
  awk '
    /^- / {
      print
    }
  '
}

repo="."
version=""
format=""

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
    --format)
      format="$2"
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

[[ -n "$version" ]] || fail "missing --version"
[[ -n "$format" ]] || fail "missing --format"

repo="$(cd "$repo" && pwd)"
release_notes="$repo/docs/docs/release-notes.md"
[[ -f "$release_notes" ]] || fail "missing docs/docs/release-notes.md"

section="$(extract_section "$release_notes" "$version")"
[[ -n "$section" ]] || fail "missing release notes section for $version"

case "$format" in
  github)
    printf '%s\n' "$section"
    ;;
  telegram-html)
    release_url="https://github.com/open-tool/ultron/releases/tag/$version"
    printf '<b>Ultron %s</b>\n\n' "$(printf '%s' "$version" | html_escape)"
    printf '<b>Highlights</b>\n'
    highlights="$(printf '%s\n' "$section" | extract_highlights | sed 's/^- //')"
    if [[ -n "$highlights" ]]; then
      while IFS= read -r line; do
        [[ -z "$line" ]] && continue
        printf '• %s\n' "$(printf '%s' "$line" | html_escape)"
      done <<<"$highlights"
    else
      printf '• See the GitHub Release for details.\n'
    fi
    printf '\n<a href="%s">GitHub Release</a>\n\n' "$release_url"
    printf '<b>Maven coordinates</b>\n'
    printf '<code>com.atiurin:ultron-compose:%s</code>\n' "$(printf '%s' "$version" | html_escape)"
    printf '<code>com.atiurin:ultron-android:%s</code>\n' "$(printf '%s' "$version" | html_escape)"
    printf '<code>com.atiurin:ultron-allure:%s</code>\n' "$(printf '%s' "$version" | html_escape)"
    ;;
  *)
    usage
    exit 2
    ;;
esac

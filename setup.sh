#!/usr/bin/env bash
# setup.sh — Symlink src/ and third_party/ from xenia-upstream into the repo root.
#
# xenia-upstream can be present either as:
#   a) A git submodule:  git submodule update --init --depth 1 xenia-upstream
#   b) A direct clone:   git clone --depth 1 https://github.com/xenia-project/xenia.git xenia-upstream

set -euo pipefail
REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
UPSTREAM="${REPO}/xenia-upstream"

if [[ ! -d "${UPSTREAM}/src" ]]; then
  echo "xenia-upstream not found. Cloning..."
  git clone --depth 1 --branch master \
    https://github.com/xenia-project/xenia.git \
    "${UPSTREAM}"
fi

for target in src third_party; do
  link="${REPO}/${target}"
  src="${UPSTREAM}/${target}"
  if [[ -L "${link}" ]]; then
    echo "  already linked: ${target}"
  elif [[ -e "${link}" ]]; then
    echo "  WARNING: ${link} exists as real path, skipping"
  else
    ln -s "${src}" "${link}"
    echo "  linked: ${target}"
  fi
done

echo "Done. Run: ./gradlew assembleDebug"

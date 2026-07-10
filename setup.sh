#!/usr/bin/env bash
# setup.sh — Symlink the xenia-upstream submodule's src/ and third_party/
# directories into the repo root so CMake can find them without copying.
#
# Run once after `git clone --recurse-submodules`.

set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
UPSTREAM="${SCRIPT_DIR}/xenia-upstream"

if [[ ! -d "$UPSTREAM/src" ]]; then
  echo "ERROR: xenia-upstream submodule not initialised."
  echo "Run: git submodule update --init --recursive"
  exit 1
fi

# Create symlinks if they don't exist yet.
for target in src third_party; do
  link="${SCRIPT_DIR}/${target}"
  if [[ -L "$link" ]]; then
    echo "  symlink already exists: $target -> xenia-upstream/$target"
  elif [[ -e "$link" ]]; then
    echo "  WARNING: $link already exists as a non-symlink, skipping"
  else
    ln -s "${UPSTREAM}/${target}" "${link}"
    echo "  created symlink: $target -> xenia-upstream/$target"
  fi
done

echo ""
echo "Setup complete. You can now open the project in Android Studio or run:"
echo "  ./gradlew assembleDebug"

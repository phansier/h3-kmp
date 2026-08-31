#!/usr/bin/env bash
#
# Re-vendors the Uber H3 C library into this repo at the version pinned in the
# `H3_VERSION` file, the way h3-go does it:
# https://github.com/uber/h3-go/blob/master/update-h3.sh
#
# Unlike h3-go we keep upstream's `h3lib/{lib,include}` layout, so no `#include`
# rewriting is needed - includes resolve through target_include_directories().
#
# Arguments: [git-remote]
#
#   git-remote - git remote to clone H3 from. Defaults to
#                https://github.com/uber/h3.git
#
# To bump the version:
#
#   echo v4.5.0 > H3_VERSION
#   make updateH3
#
# The CMake source lists are globbed, so files added or dropped upstream need no
# build-file edit. What is NOT automatic:
#
#   - androidLibrary/src/main/cpp/jniapi.cpp wraps three H3 functions by hand
#     (latLngToCell, cellToBoundary, areNeighborCells). Check them if a major
#     version changes signatures.
#   - the three prebuilt cinterop/h3/*/libh3.a need macOS; this script rebuilds
#     them there and warns everywhere else.

set -euo pipefail

GIT_REMOTE="${1:-https://github.com/uber/h3.git}"

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$REPO_ROOT"

H3_DEST="androidLibrary/src/main/cpp/h3lib"
CINTEROP_HEADER="cinterop/h3/headers/h3api.h"

if [[ ! -f H3_VERSION ]]; then
    echo "error: H3_VERSION not found in $REPO_ROOT" >&2
    exit 1
fi

# Tolerate a trailing newline and an optional leading "v".
H3_TAG="$(tr -d '[:space:]' < H3_VERSION)"
if [[ ! "$H3_TAG" =~ ^v?([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
    echo "error: H3_VERSION must look like 'v4.2.1', got '$H3_TAG'" >&2
    exit 1
fi
H3_MAJOR="${BASH_REMATCH[1]}"
H3_MINOR="${BASH_REMATCH[2]}"
H3_PATCH="${BASH_REMATCH[3]}"
H3_SEMVER="$H3_MAJOR.$H3_MINOR.$H3_PATCH"
# Upstream tags are v-prefixed; accept "4.5.0" in the file and normalise here.
H3_TAG="v$H3_SEMVER"

WORK_DIR="$(mktemp -d)"
cleanup() { rm -rf "$WORK_DIR"; }
trap cleanup EXIT

echo "==> Cloning $GIT_REMOTE at $H3_TAG"
git -c advice.detachedHead=false clone --quiet --depth 1 --branch "$H3_TAG" \
    "$GIT_REMOTE" "$WORK_DIR/h3"

UPSTREAM="$WORK_DIR/h3/src/h3lib"
if [[ ! -d "$UPSTREAM/lib" || ! -f "$UPSTREAM/include/h3api.h.in" ]]; then
    echo "error: $H3_TAG does not look like an H3 source tree (no src/h3lib)" >&2
    exit 1
fi

# Wipe rather than overwrite, so files dropped upstream disappear here too.
echo "==> Replacing $H3_DEST"
rm -rf "$H3_DEST"
mkdir -p "$H3_DEST/lib" "$H3_DEST/include"
cp "$UPSTREAM"/lib/*.c "$H3_DEST/lib/"
cp "$UPSTREAM"/include/*.h "$H3_DEST/include/"
cp "$UPSTREAM"/include/h3api.h.in "$H3_DEST/include/"

# Kotlin/Native cinterop parses this header directly - it never runs CMake, so
# configure_file() cannot do the substitution for it.
echo "==> Regenerating $CINTEROP_HEADER for $H3_SEMVER"
mkdir -p "$(dirname "$CINTEROP_HEADER")"
sed -e "s/@H3_VERSION_MAJOR@/$H3_MAJOR/" \
    -e "s/@H3_VERSION_MINOR@/$H3_MINOR/" \
    -e "s/@H3_VERSION_PATCH@/$H3_PATCH/" \
    "$H3_DEST/include/h3api.h.in" > "$CINTEROP_HEADER"

# wasmJs has no cinterop and bridges to the h3-js npm package instead; keep it on
# the same H3 release so all three targets expose one version. h3-js does not
# publish for every C release (there is no h3-js 4.4.1, for instance), so only
# repin when that exact version exists.
CURRENT_H3_JS="$(sed -nE 's|.*npm\("h3-js", "([^"]+)"\).*|\1|p' library/build.gradle.kts)"
if [[ "$CURRENT_H3_JS" == "$H3_SEMVER" ]]; then
    echo "==> h3-js already pinned to $H3_SEMVER"
elif curl -fsS "https://registry.npmjs.org/h3-js/$H3_SEMVER" >/dev/null 2>&1; then
    echo "==> Pinning h3-js to $H3_SEMVER in library/build.gradle.kts"
    sed -i.bak -E "s|npm\\(\"h3-js\", \"[^\"]+\"\\)|npm(\"h3-js\", \"$H3_SEMVER\")|" library/build.gradle.kts
    rm library/build.gradle.kts.bak
    # A changed npm dependency invalidates the committed lock file, and
    # kotlinWasmStoreYarnLock then fails every wasm task until it is refreshed.
    echo "==> Refreshing kotlin-js-store/yarn.lock"
    ./gradlew --quiet kotlinWasmUpgradeYarnLock
else
    echo "!!! h3-js $H3_SEMVER is not on npm; leaving the wasmJs pin at $CURRENT_H3_JS."
fi

echo "==> Updating H3 version in README.md"
sed -i.bak -E "/uber\\/h3\\/releases\\/tag/ s|v?[0-9]+\\.[0-9]+\\.[0-9]+|$H3_TAG|g" README.md
rm README.md.bak

if [[ "$(uname -s)" == "Darwin" ]] && command -v cmake >/dev/null 2>&1; then
    echo "==> Rebuilding cinterop/h3/*/libh3.a"
    make buildIosDotA
else
    echo
    echo "!!! libh3.a NOT rebuilt: needs macOS with cmake and the iOS SDKs."
    echo "!!! Run 'make buildIosDotA' on a Mac and commit cinterop/h3/*/libh3.a"
    echo "!!! plus cinterop/h3/SOURCES.sha256 together with these sources, or the"
    echo "!!! iOS build links a stale H3. CI checks that fingerprint."
fi

echo
echo "==> H3 $H3_TAG vendored. Changed files:"
git status --short
echo
echo "Next, verify all three targets still build against it:"
echo "  ./gradlew :androidLibrary:assembleDebug \\"
echo "            :library:iosSimulatorArm64Test \\"
echo "            :library:linkDebugTestIosArm64 :library:linkDebugTestIosX64 \\"
echo "            :library:wasmJsNodeTest"

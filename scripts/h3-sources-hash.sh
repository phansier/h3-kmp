#!/usr/bin/env bash
#
# Prints one hash covering everything that decides the contents of the committed
# cinterop/h3/<target>/libh3.a archives: the pinned H3 version, the vendored C
# sources, and the CMake files that pick which of them get compiled.
#
# `make buildIosDotA` records the result in cinterop/h3/SOURCES.sha256, so CI can
# tell whether the archives were rebuilt after a source change.
#
# Why a source hash and not a comparison of the archives themselves:
#   - byte comparison always differs, because ar records member timestamps;
#   - symbol names are identical across all three architectures, and identical
#     across any change that does not add or remove a function - so comparing
#     them misses a changed implementation entirely;
#   - hashing the object files would fail whenever the runner's Xcode differs
#     from the committer's.
#
# Not covered: the compiler flags in the Makefile itself (iosMinVersion, the
# per-target sysroot/arch). Changing those needs a manual `make buildIosDotA`.

set -euo pipefail

cd "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if command -v shasum >/dev/null 2>&1; then
    sha() { shasum -a 256 "$@"; }
else
    sha() { sha256sum "$@"; }
fi

{
    cat H3_VERSION
    # Hash each path together with its name, so a rename counts as a change.
    # LC_ALL=C sort keeps the order stable across machines.
    find androidLibrary/src/main/cpp/h3lib cinterop/h3/cmake -type f \
        \( -name '*.c' -o -name '*.h' -o -name '*.in' -o -name '*.cmake' -o -name 'CMakeLists.txt' \) \
        | LC_ALL=C sort \
        | while IFS= read -r f; do sha "$f"; done
} | sha | cut -d' ' -f1

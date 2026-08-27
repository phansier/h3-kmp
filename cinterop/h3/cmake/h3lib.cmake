# Resolves the vendored H3 C sources and their version. Included by both native
# builds: the Android one (SHARED, plus jniapi.cpp) and the Kotlin/Native one
# (STATIC). Only the target kind differs, so the file list lives here once.
#
# Callers must set, before include():
#   H3_SRC       - the androidLibrary/src/main/cpp directory
#   H3_REPO_ROOT - the repo root, where the H3_VERSION file lives
#
# Provides: H3_LIB_SOURCES, H3_LIB_HEADERS, H3_VERSION_{MAJOR,MINOR,PATCH}, and a
# configured h3api.h under ${CMAKE_CURRENT_BINARY_DIR}/h3lib/include.

if (NOT DEFINED H3_SRC OR NOT DEFINED H3_REPO_ROOT)
    message(FATAL_ERROR "h3lib.cmake requires H3_SRC and H3_REPO_ROOT to be set")
endif ()

# The single source of truth for the vendored version; scripts/update-h3.sh reads
# the same file to decide which upstream tag to copy in.
file(READ ${H3_REPO_ROOT}/H3_VERSION H3_VERSION_RAW)
if (NOT H3_VERSION_RAW MATCHES "([0-9]+)\\.([0-9]+)\\.([0-9]+)")
    message(FATAL_ERROR "Cannot parse a version out of ${H3_REPO_ROOT}/H3_VERSION")
endif ()
set(H3_VERSION_MAJOR ${CMAKE_MATCH_1})
set(H3_VERSION_MINOR ${CMAKE_MATCH_2})
set(H3_VERSION_PATCH ${CMAKE_MATCH_3})

# Globbed on purpose: an H3 bump adds and removes upstream files (4.2.1 -> 4.5.0
# adds area.c and cellsToMultiPoly.c, drops vertexGraph.c), and a hand-written
# list silently skips new sources - which surfaces only as a link error.
# CONFIGURE_DEPENDS re-globs on rebuild.
file(GLOB H3_LIB_SOURCES CONFIGURE_DEPENDS ${H3_SRC}/h3lib/lib/*.c)
file(GLOB H3_LIB_HEADERS CONFIGURE_DEPENDS ${H3_SRC}/h3lib/include/*.h)
if (NOT H3_LIB_SOURCES)
    message(FATAL_ERROR "No H3 sources under ${H3_SRC}/h3lib/lib - run 'make updateH3'")
endif ()

# h3api.h is generated, never committed. The Kotlin/Native cinterop copy at
# cinterop/h3/headers/h3api.h is written by scripts/update-h3.sh instead, because
# cinterop does not run CMake.
configure_file(${H3_SRC}/h3lib/include/h3api.h.in
        ${CMAKE_CURRENT_BINARY_DIR}/h3lib/include/h3api.h)

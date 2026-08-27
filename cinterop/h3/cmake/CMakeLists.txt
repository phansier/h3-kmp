# Builds libh3.a for Kotlin/Native. The C sources are shared with the Android
# build via h3lib.cmake; only the target kind differs (STATIC, and no JNI layer).
#
# Invoked per target by `make buildIosDotA`, never by AGP.

cmake_minimum_required(VERSION 3.22.1)

project(h3kmp)

set(H3_SRC ${CMAKE_CURRENT_SOURCE_DIR}/../../../androidLibrary/src/main/cpp)
set(H3_REPO_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/../../..)
include(${CMAKE_CURRENT_SOURCE_DIR}/h3lib.cmake)

add_library(${CMAKE_PROJECT_NAME} STATIC
        # H3_LIB_HEADERS is listed only so the IDE indexes them.
        ${H3_LIB_SOURCES}
        ${H3_LIB_HEADERS}
)
target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC
        $<BUILD_INTERFACE:${H3_SRC}/h3lib/include>
        $<BUILD_INTERFACE:${CMAKE_CURRENT_BINARY_DIR}/h3lib/include>)

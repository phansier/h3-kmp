# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

project(h3kmp)

# The vendored H3 sources live next to this file; the version file is at the repo
# root. cinterop/h3/cmake/h3lib.cmake globs the sources so an H3 bump needs no
# edit here - see scripts/update-h3.sh.
set(H3_SRC ${CMAKE_CURRENT_SOURCE_DIR})
set(H3_REPO_ROOT ${CMAKE_CURRENT_SOURCE_DIR}/../../../..)
include(${H3_REPO_ROOT}/cinterop/h3/cmake/h3lib.cmake)

# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
add_library(${CMAKE_PROJECT_NAME} SHARED
        # H3_LIB_HEADERS is listed only so the IDE indexes them.
        ${H3_LIB_SOURCES}
        ${H3_LIB_HEADERS}
        jniapi.cpp
)
target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC $<BUILD_INTERFACE:${H3_SRC}/h3lib/include> $<BUILD_INTERFACE:${CMAKE_CURRENT_BINARY_DIR}/h3lib/include>)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log)

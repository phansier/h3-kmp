# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

project(h3kmp)

set(LIB_SOURCE_FILES
        h3lib/include/h3Assert.h
        h3lib/include/alloc.h
        h3lib/include/bbox.h
        h3lib/include/polygon.h
        h3lib/include/polygonAlgos.h
        h3lib/include/polyfill.h
        h3lib/include/h3Index.h
        h3lib/include/directedEdge.h
        h3lib/include/latLng.h
        h3lib/include/vec2d.h
        h3lib/include/vec3d.h
        h3lib/include/linkedGeo.h
        h3lib/include/localij.h
        h3lib/include/baseCells.h
        h3lib/include/faceijk.h
        h3lib/include/vertex.h
        h3lib/include/vertexGraph.h
        h3lib/include/mathExtensions.h
        h3lib/include/iterators.h
        h3lib/include/constants.h
        h3lib/include/coordijk.h
        h3lib/include/algos.h
        h3lib/lib/h3Assert.c
        h3lib/lib/algos.c
        h3lib/lib/coordijk.c
        h3lib/lib/bbox.c
        h3lib/lib/polygon.c
        h3lib/lib/polyfill.c
        h3lib/lib/h3Index.c
        h3lib/lib/vec2d.c
        h3lib/lib/vec3d.c
        h3lib/lib/vertex.c
        h3lib/lib/linkedGeo.c
        h3lib/lib/localij.c
        h3lib/lib/latLng.c
        h3lib/lib/directedEdge.c
        h3lib/lib/mathExtensions.c
        h3lib/lib/iterators.c
        h3lib/lib/vertexGraph.c
        h3lib/lib/faceijk.c
        h3lib/lib/baseCells.c)

configure_file(${CMAKE_CURRENT_SOURCE_DIR}/h3lib/include/h3api.h.in ${CMAKE_CURRENT_BINARY_DIR}/h3lib/include/h3api.h)

# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;

add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
        ${LIB_SOURCE_FILES}
        jniapi.cpp
)
target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/h3lib/include> $<BUILD_INTERFACE:${CMAKE_CURRENT_BINARY_DIR}/h3lib/include>)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log)
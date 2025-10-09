# this is a copy of CMakeLists.txt from /cpp
# adopted to build .a for iOS
# next only changes will be highlighted

cmake_minimum_required(VERSION 3.22.1)

project(h3kmp)

# added cpp to paths here

set(LIB_SOURCE_FILES
        cpp/h3lib/include/h3Assert.h
        cpp/h3lib/include/alloc.h
        cpp/h3lib/include/bbox.h
        cpp/h3lib/include/polygon.h
        cpp/h3lib/include/polygonAlgos.h
        cpp/h3lib/include/polyfill.h
        cpp/h3lib/include/h3Index.h
        cpp/h3lib/include/directedEdge.h
        cpp/h3lib/include/latLng.h
        cpp/h3lib/include/vec2d.h
        cpp/h3lib/include/vec3d.h
        cpp/h3lib/include/linkedGeo.h
        cpp/h3lib/include/localij.h
        cpp/h3lib/include/baseCells.h
        cpp/h3lib/include/faceijk.h
        cpp/h3lib/include/vertex.h
        cpp/h3lib/include/vertexGraph.h
        cpp/h3lib/include/mathExtensions.h
        cpp/h3lib/include/iterators.h
        cpp/h3lib/include/constants.h
        cpp/h3lib/include/coordijk.h
        cpp/h3lib/include/algos.h
        cpp/h3lib/lib/h3Assert.c
        cpp/h3lib/lib/algos.c
        cpp/h3lib/lib/coordijk.c
        cpp/h3lib/lib/bbox.c
        cpp/h3lib/lib/polygon.c
        cpp/h3lib/lib/polyfill.c
        cpp/h3lib/lib/h3Index.c
        cpp/h3lib/lib/vec2d.c
        cpp/h3lib/lib/vec3d.c
        cpp/h3lib/lib/vertex.c
        cpp/h3lib/lib/linkedGeo.c
        cpp/h3lib/lib/localij.c
        cpp/h3lib/lib/latLng.c
        cpp/h3lib/lib/directedEdge.c
        cpp/h3lib/lib/mathExtensions.c
        cpp/h3lib/lib/iterators.c
        cpp/h3lib/lib/vertexGraph.c
        cpp/h3lib/lib/faceijk.c
        cpp/h3lib/lib/baseCells.c)

# added cpp to paths here
configure_file(${CMAKE_CURRENT_SOURCE_DIR}/cpp/h3lib/include/h3api.h.in ${CMAKE_CURRENT_BINARY_DIR}/cpp/h3lib/include/h3api.h)

# STATIC here
# removed jniapi.cpp

add_library(${CMAKE_PROJECT_NAME} STATIC
        ${LIB_SOURCE_FILES}
)
# added cpp to paths here
target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/cpp/h3lib/include> $<BUILD_INTERFACE:${CMAKE_CURRENT_BINARY_DIR}/cpp/h3lib/include>)

# removed target_link_libraries

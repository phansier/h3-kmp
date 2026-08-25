# Builds libh3.a for Kotlin/Native. The C sources are shared with the Android
# build; only the target kind differs (STATIC, and no JNI layer). The file list
# below is copied verbatim from androidLibrary/src/main/cpp/CMakeLists.txt so a
# plain diff shows any drift.
#
# Invoked per target by `make buildIosDotA`, never by AGP.

cmake_minimum_required(VERSION 3.22.1)

project(h3kmp)

set(H3_SRC ${CMAKE_CURRENT_SOURCE_DIR}/../../../androidLibrary/src/main/cpp)

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
list(TRANSFORM LIB_SOURCE_FILES PREPEND ${H3_SRC}/)

configure_file(${H3_SRC}/h3lib/include/h3api.h.in ${CMAKE_CURRENT_BINARY_DIR}/h3lib/include/h3api.h)

add_library(${CMAKE_PROJECT_NAME} STATIC
        ${LIB_SOURCE_FILES}
)
target_include_directories(${CMAKE_PROJECT_NAME} PUBLIC
        $<BUILD_INTERFACE:${H3_SRC}/h3lib/include>
        $<BUILD_INTERFACE:${CMAKE_CURRENT_BINARY_DIR}/h3lib/include>)

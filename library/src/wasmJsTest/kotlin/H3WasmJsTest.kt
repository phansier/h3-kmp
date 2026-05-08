package com.beriukhov.h3

import boundaries
import com.beriukhov.h3.H3.Companion.geoToH3
import com.beriukhov.h3.H3.Companion.vertices
import zeroLatLngRes4
import kotlin.test.Test
import kotlin.test.assertEquals

class H3WasmJsTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testGeoToH3() {
        assertEquals(zeroLatLngRes4, geoToH3(LatLng(0.0, 0.0), res = 4).toHexString())
    }

    @Test
    fun testVertices() {
        assertEquals(
            boundaries,
            vertices(zeroLatLngRes4, 8)
        )
    }
}

package com.beriukhov.h3

import boundaries
import com.beriukhov.h3.H3.Companion.geoToH3
import com.beriukhov.h3.H3.Companion.vertices
import neighborOfZeroLatLngRes4
import notNeighborOfZeroLatLngRes4
import zeroLatLngRes4
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IosH3Test {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testGeoToH3() {
        assertEquals(zeroLatLngRes4, geoToH3(LatLng(0.0, 0.0), res = 4).toHexString())
    }

    // actually 7 decimal places are enough (~1.1 cm accuracy)
    @Test
    fun testVertices() {
        assertEquals(
            boundaries,
            vertices(zeroLatLngRes4, 8)
        )
    }

    @Test
    fun testAreNeighborCells() {
        assertTrue(H3.areNeighborCells(zeroLatLngRes4, neighborOfZeroLatLngRes4))
        assertFalse(H3.areNeighborCells(zeroLatLngRes4, notNeighborOfZeroLatLngRes4))
        assertFalse(H3.areNeighborCells(zeroLatLngRes4, zeroLatLngRes4))
    }

}

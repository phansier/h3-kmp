package com.beriukhov.h3

import h3.CellBoundary
import h3.cellToBoundary
import h3.latLngToCell
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ULongVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import h3.LatLng as H3LatLng

actual class H3 {

    @OptIn(ExperimentalForeignApi::class)
    actual companion object {

        /**
         * Find the H3 index of the resolution <code>res</code> cell containing the lat/lon (in degrees)
         *
         * @param lat Latitude in degrees.
         * @param lng Longitude in degrees.
         * @param res Resolution, 0 &lt;= res &lt;= 15
         * @return The H3 index.
         * @throws IllegalArgumentException latitude, longitude, or resolution are out of range.
         */
        actual fun geoToH3(geo: LatLng, res: Int): ULong {
            checkResolution(res)
            memScoped {
                val g = alloc<H3LatLng>()
                g.lat = toRadians(geo.lat)
                g.lng = toRadians(geo.lng)
                val out = alloc<ULongVar>()
                latLngToCell(
                    g = g.ptr,
                    res = res,
                    out = out.ptr
                )
                require(out.value != INVALID_INDEX.toULong()) { "Latitude or longitude were invalid." }
                return out.value
            }
        }

        /** Find the cell boundary in latitude, longitude (degrees) coordinates for the cell */
        actual fun vertices(h3Index: String, decimalsToRound: Int): List<LatLng> {
            val h3: ULong = stringToH3(h3Index)
            memScoped {
                val out = alloc<CellBoundary>()
                cellToBoundary(h3, out.ptr)
                val list: List<LatLng> = buildList {
                    for (i in 0 until out.numVerts) {
                        val h3LatLng = out.verts.get(i)
                        add(
                            LatLng(
                                lat = toDegrees(
                                    angrad = h3LatLng.lat,
                                    decimalsToRound = decimalsToRound
                                ),
                                lng = toDegrees(
                                    angrad = h3LatLng.lng,
                                    decimalsToRound = decimalsToRound
                                )
                            )
                        )
                    }
                }
                return list
            }
        }

        private const val INVALID_INDEX: Long = 0L

        /**
         * @throws IllegalArgumentException `res` is not a valid H3 resolution.
         */
        private fun checkResolution(res: Int) {
            require(!(res < 0 || res > 15)) {
                "resolution $res is out of range (must be 0 <= res <= 15)"
            }
        }

        private fun stringToH3(h3Address: String): ULong =
            h3Address.toULong(radix = 16)
    }
}

private const val DEGREES_TO_RADIANS = 0.017453292519943295
private const val RADIANS_TO_DEGREES = 57.29577951308232

private fun toRadians(angdeg: Double): Double {
    return angdeg * DEGREES_TO_RADIANS
}
private fun toDegrees(angrad: Double, decimalsToRound: Int): Double {
    return (angrad * RADIANS_TO_DEGREES).roundToDecimals(decimalsToRound)
}
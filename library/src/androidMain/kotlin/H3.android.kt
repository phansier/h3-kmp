package com.beriukhov.h3

import java.util.Locale


actual class H3 {

    actual companion object {

        private val jni by lazy {Jni()}

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
            val result: Long = jni.latLngToCell(Math.toRadians(geo.lat), Math.toRadians(geo.lng), res)
            require(result != INVALID_INDEX) { "Latitude or longitude were invalid." }
            return result.toULong()
        }

        /** Find the cell boundary in latitude, longitude (degrees) coordinates for the cell */
        actual fun vertices(h3Index: String, decimalsToRound: Int): List<LatLng> {
            val h3: Long = stringToH3(h3Index).toLong()
            val verts = DoubleArray(MAX_CELL_BNDRY_VERTS * 2)
            val numVerts: Int = jni.cellToBoundary(h3, verts)
            val out = buildList{
                for (i in 0..<numVerts) {
                    val coord = LatLng(
                        lat = Math.toDegrees(verts[i * 2])
                            .roundToDecimals(decimalsToRound),
                        lng = Math.toDegrees(verts[(i * 2) + 1])
                            .roundToDecimals(decimalsToRound)
                    )
                    add(coord)
                }
            }
            return out
        }

        private const val INVALID_INDEX: Long = 0L
        private const val MAX_CELL_BNDRY_VERTS = 10

        /**
         * @throws IllegalArgumentException `res` is not a valid H3 resolution.
         */
        private fun checkResolution(res: Int) {
            require(!(res < 0 || res > 15)) {
                String.format(
                    Locale.getDefault(),
                    "resolution %d is out of range (must be 0 <= res <= 15)",
                    res
                )
            }
        }
        private fun stringToH3(h3Address: String): ULong =
            h3Address.toULong(radix = 16)
    }
}
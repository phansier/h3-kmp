package com.beriukhov.h3

import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
actual class H3 {

    actual companion object {

        actual fun geoToH3(geo: LatLng, res: Int): ULong {
            checkResolution(res)
            val hex = latLngToCell(geo.lat, geo.lng, res)
            return hex.toULong(radix = 16)
        }

        actual fun vertices(h3Index: String, decimalsToRound: Int): List<LatLng> {
            val boundary = cellToBoundary(h3Index)
            return buildList {
                for (i in 0 until boundary.length) {
                    val pair = boundary[i]!!
                    val lat = pair[0]!!.toDouble()
                    val lng = pair[1]!!.toDouble()
                    add(
                        LatLng(
                            lat = lat.roundToDecimals(decimalsToRound),
                            lng = lng.roundToDecimals(decimalsToRound),
                        )
                    )
                }
            }
        }

        private fun checkResolution(res: Int) {
            require(!(res < 0 || res > 15)) {
                "resolution $res is out of range (must be 0 <= res <= 15)"
            }
        }
    }
}

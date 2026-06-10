package com.beriukhov.h3

import kotlin.math.pow
import kotlin.math.round

expect class H3 {

    companion object {
        fun geoToH3(geo: LatLng, res: Int): ULong
        fun vertices(h3Index: String, decimalsToRound: Int = DEFAULT_DECIMALS_TO_ROUND): List<LatLng>

        /**
         * Returns whether or not the two provided cells border each other.
         *
         * @param origin First H3 index.
         * @param destination Second H3 index.
         * @return `true` if the cells are neighbors, `false` otherwise.
         * @throws H3Exception (or platform equivalent) if the indexes are invalid
         *   or are at different resolutions.
         */
        fun areNeighborCells(origin: ULong, destination: ULong): Boolean
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun H3.Companion.h3ToString(h3: ULong): String = h3.toLong().toHexString()

fun H3.Companion.geoToH3String(geo: LatLng, res: Int) = h3ToString(geoToH3(geo, res))

fun H3.Companion.polygon(h3Index: String) = Polygon(h3Index, vertices(h3Index))

fun H3.Companion.polygons(h3Indexes: List<String>) = h3Indexes.map { Polygon(it, vertices(it)) }

/** Returns whether or not the two provided cells (as hex strings) border each other. */
fun H3.Companion.areNeighborCells(origin: String, destination: String): Boolean =
    areNeighborCells(origin.toULong(radix = 16), destination.toULong(radix = 16))

fun Double.roundToDecimals(decimals: Int = DEFAULT_DECIMALS_TO_ROUND): Double {
    val factor = 10.0.pow(decimals)
    return round(this * factor) / factor
}

internal fun List<LatLng>.roundToDecimals(decimals: Int): List<LatLng> =
    this.map {
        it.copy(
            lat = it.lat.roundToDecimals(decimals),
            lng = it.lng.roundToDecimals(decimals)
        )
    }

const val DEFAULT_DECIMALS_TO_ROUND = 8

package com.beriukhov.h3

import kotlin.math.pow
import kotlin.math.round

expect class H3 {

    companion object {
        fun geoToH3(geo: LatLng, res: Int): ULong
        fun vertices(h3Index: String, decimalsToRound: Int = DEFAULT_DECIMALS_TO_ROUND): List<LatLng>
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun H3.Companion.h3ToString(h3: ULong): String = h3.toLong().toHexString()

fun H3.Companion.geoToH3String(geo: LatLng, res: Int) = h3ToString(geoToH3(geo, res))

fun H3.Companion.polygon(h3Index: String) = Polygon(h3Index, vertices(h3Index))

fun H3.Companion.polygons(h3Indexes: List<String>) = h3Indexes.map { Polygon(it, vertices(it)) }

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

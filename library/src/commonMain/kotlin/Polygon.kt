package com.beriukhov.h3

data class Polygon(
    val h3Index: String,
    val vertices: List<LatLng> = emptyList(),
)
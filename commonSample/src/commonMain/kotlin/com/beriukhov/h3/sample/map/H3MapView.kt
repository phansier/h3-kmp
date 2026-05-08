package com.beriukhov.h3.sample.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.beriukhov.h3.LatLng

//private const val DEFAULT_STYLE_URL = "https://demotiles.maplibre.org/style.json"
const val DEFAULT_STYLE_URL: String = "https://tiles.openfreemap.org/styles/liberty"

@Composable
expect fun H3MapView(
    vertices: List<LatLng>,
    modifier: Modifier = Modifier,
)

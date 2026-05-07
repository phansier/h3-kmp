package com.beriukhov.h3.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.beriukhov.h3.H3
import com.beriukhov.h3.LatLng
import com.beriukhov.h3.geoToH3String

class MapDemoState(
    initialLatLng: String,
    initialRes: String,
    initialH3Input: String,
) {
    var latLngText: String by mutableStateOf(initialLatLng)
    var resText: String by mutableStateOf(initialRes)
    var h3InputText: String by mutableStateOf(initialH3Input)

    val parsedLat: Double? get() = latLngText.split(',').firstOrNull()?.trim()?.toDoubleOrNull()?.takeIf { it in -90.0..90.0 }
    val parsedLng: Double? get() = latLngText.split(',').getOrNull(1)?.trim()?.toDoubleOrNull()?.takeIf { it in -180.0..180.0 }
    val parsedRes: Int? get() = resText.toIntOrNull()?.takeIf { it in 0..15 }

    val geoToH3Result: String? by derivedStateOf {
        val lat = parsedLat
        val lng = parsedLng
        val res = parsedRes
        if (lat != null && lng != null && res != null) {
            runCatching { H3.geoToH3String(LatLng(lat, lng), res) }.getOrNull()
        } else null
    }

    val verticesForH3Input: List<LatLng> by derivedStateOf {
        val text = h3InputText.trim()
        if (text.isEmpty()) emptyList()
        else runCatching { H3.vertices(text) }.getOrDefault(emptyList())
    }

    fun copyGeneratedIndexToInput() {
        geoToH3Result?.let { h3InputText = it }
    }
}

@Composable
fun rememberMapDemoState(): MapDemoState = remember {
    MapDemoState(
        initialLatLng = "51.5074, -0.1278",
        initialRes = "9",
        initialH3Input = "",
    )
}

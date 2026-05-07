package com.beriukhov.h3.sample.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.beriukhov.h3.LatLng
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Position

// todo reuse through mobileMain sourceset
@Composable
actual fun H3MapView(
    vertices: List<LatLng>,
    modifier: Modifier,
) {
    val camera = rememberCameraState(
        firstPosition = CameraPosition(
            target = Position(longitude = -0.1278, latitude = 51.5074),
            zoom = 8.0,
        ),
    )



    LaunchedEffect(vertices) {
        val bounds = vertices.boundingBox() ?: return@LaunchedEffect
        camera.animateTo(boundingBox = bounds)
    }

    MaplibreMap(
        modifier = modifier,
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        cameraState = camera,
    ) {
        val geoJson = vertices.toFeatureCollectionJson()
        val source = rememberGeoJsonSource(GeoJsonData.JsonString(geoJson))
        FillLayer(
            id = "h3-fill",
            source = source,
            color = const(Color(0xFF0066FF)),
            opacity = const(0.25f),
        )
        LineLayer(
            id = "h3-outline",
            source = source,
            color = const(Color(0xFF0066FF)),
            width = const(2.dp),
        )
    }
}

private fun List<LatLng>.toFeatureCollectionJson(): String {
    if (isEmpty()) return """{"type":"FeatureCollection","features":[]}"""
    val ring = (this + first()).joinToString(",") { "[${it.lng},${it.lat}]" }
    return """{"type":"FeatureCollection","features":[{"type":"Feature","properties":{},"geometry":{"type":"Polygon","coordinates":[[$ring]]}}]}"""
}

fun List<LatLng>.boundingBox(): BoundingBox? {
    if (isEmpty()) return null
    var minLat = first().lat
    var maxLat = first().lat
    var minLng = first().lng
    var maxLng = first().lng
    for (v in this) {
        if (v.lat < minLat) minLat = v.lat
        if (v.lat > maxLat) maxLat = v.lat
        if (v.lng < minLng) minLng = v.lng
        if (v.lng > maxLng) maxLng = v.lng
    }
    val padLat = ((maxLat - minLat) * 0.5).coerceAtLeast(0.001)
    val padLng = ((maxLng - minLng) * 0.5).coerceAtLeast(0.001)
    return BoundingBox(
        west = (minLng - padLng).coerceAtLeast(-180.0),
        south = (minLat - padLat).coerceAtLeast(-90.0),
        east = (maxLng + padLng).coerceAtMost(180.0),
        north = (maxLat + padLat).coerceAtMost(90.0),
    )
}

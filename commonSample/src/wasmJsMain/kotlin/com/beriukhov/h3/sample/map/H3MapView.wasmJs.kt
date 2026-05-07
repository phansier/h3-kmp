@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package com.beriukhov.h3.sample.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import com.beriukhov.h3.LatLng
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.HTMLScriptElement

private const val MAPLIBRE_VERSION = "4.7.1"
//private const val DEFAULT_STYLE_URL = "https://demotiles.maplibre.org/style.json"
private const val DEFAULT_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"

private var nextMapId: Int = 0
private var maplibreLoadStarted: Boolean = false
private val maplibreLoadCallbacks: MutableList<() -> Unit> = mutableListOf()

@Composable
actual fun H3MapView(
    vertices: List<LatLng>,
    modifier: Modifier,
) {
    val containerId = remember { "h3-map-${nextMapId++}" }
    val state = remember { WasmMapState(containerId) }

    DisposableEffect(state) {
        state.attach()
        onDispose { state.detach() }
    }

    LaunchedEffect(vertices) {
        state.updateVertices(vertices)
    }

    Box(
        modifier = modifier
            .background(Color(0xFFE6E6E6))
            .onGloballyPositioned { coords ->
                val p = coords.positionInWindow()
                val dpr = window.devicePixelRatio.takeIf { it > 0.0 } ?: 1.0
                state.updateGeometry(
                    x = p.x.toDouble() / dpr,
                    y = p.y.toDouble() / dpr,
                    w = coords.size.width.toDouble() / dpr,
                    h = coords.size.height.toDouble() / dpr,
                )
            }
            .fillMaxSize()
    )
}

private class WasmMapState(private val containerId: String) {
    private var div: HTMLDivElement? = null
    private var map: JsAny? = null
    private var ready: Boolean = false
    private var pending: List<LatLng> = emptyList()
    private var lastX = Double.NaN
    private var lastY = Double.NaN
    private var lastW = Double.NaN
    private var lastH = Double.NaN

    fun attach() {
        val d = (document.createElement("div") as HTMLDivElement).apply {
            id = containerId
            style.position = "fixed"
            style.left = "0px"
            style.top = "0px"
            style.width = "0px"
            style.height = "0px"
            style.zIndex = "2147483647"
            style.background = "#e6e6e6"
        }
        document.documentElement?.appendChild(d)
        div = d
    }

    fun detach() {
        map?.let { jsRemoveMap(it) }
        map = null
        div?.remove()
        div = null
        ready = false
    }

    fun updateVertices(vertices: List<LatLng>) {
        pending = vertices
        if (ready) applyPending()
    }

    fun updateGeometry(x: Double, y: Double, w: Double, h: Double) {
        if (x == lastX && y == lastY && w == lastW && h == lastH) return
        lastX = x; lastY = y; lastW = w; lastH = h
        div?.let {
            it.style.left = "${x}px"
            it.style.top = "${y}px"
            it.style.width = "${w}px"
            it.style.height = "${h}px"
        }
        if (w <= 0.0 || h <= 0.0) return
        if (map == null) {
            ensureMaplibreLoaded {
                val d = div ?: return@ensureMaplibreLoaded
                if (map != null) return@ensureMaplibreLoaded
                val m = jsCreateMap(containerId, DEFAULT_STYLE_URL)
                map = m
                jsObserveResize(d, m)
                jsAttachLoadHandler(m) {
                    ready = true
                    jsResizeMap(m)
                    applyPending()
                }
            }
        } else {
            jsResizeMap(map!!)
        }
    }

    private fun applyPending() {
        val m = map ?: return
        jsSetSourceData(m, verticesToFeatureCollection(pending))
        if (pending.isNotEmpty()) {
            val b = paddedBounds(pending)
            jsFitBounds(m, b.west, b.south, b.east, b.north)
        }
    }
}

private data class Bounds(val west: Double, val south: Double, val east: Double, val north: Double)

private fun paddedBounds(vertices: List<LatLng>): Bounds {
    var minLat = vertices[0].lat; var maxLat = vertices[0].lat
    var minLng = vertices[0].lng; var maxLng = vertices[0].lng
    for (v in vertices) {
        if (v.lat < minLat) minLat = v.lat
        if (v.lat > maxLat) maxLat = v.lat
        if (v.lng < minLng) minLng = v.lng
        if (v.lng > maxLng) maxLng = v.lng
    }
    val padLat = ((maxLat - minLat) * 0.5).coerceAtLeast(0.001)
    val padLng = ((maxLng - minLng) * 0.5).coerceAtLeast(0.001)
    return Bounds(minLng - padLng, minLat - padLat, maxLng + padLng, maxLat + padLat)
}

private fun verticesToFeatureCollection(vertices: List<LatLng>): String {
    if (vertices.isEmpty()) return """{"type":"FeatureCollection","features":[]}"""
    val ring = (vertices + vertices.first()).joinToString(",") { "[${it.lng},${it.lat}]" }
    return """{"type":"FeatureCollection","features":[{"type":"Feature","properties":{},"geometry":{"type":"Polygon","coordinates":[[$ring]]}}]}"""
}

private fun ensureMaplibreLoaded(callback: () -> Unit) {
    if (jsMaplibreReady()) {
        callback()
        return
    }
    maplibreLoadCallbacks.add(callback)
    if (maplibreLoadStarted) return
    maplibreLoadStarted = true

    val cssHref = "https://unpkg.com/maplibre-gl@$MAPLIBRE_VERSION/dist/maplibre-gl.css"
    val jsSrc = "https://unpkg.com/maplibre-gl@$MAPLIBRE_VERSION/dist/maplibre-gl.js"

    // Inline critical layout rules so the map renders even if the external CSS is blocked.
    jsInjectCriticalCss()

    val link = (document.createElement("link") as HTMLLinkElement).apply {
        rel = "stylesheet"
        href = cssHref
    }
    document.head?.appendChild(link)

    val script = (document.createElement("script") as HTMLScriptElement).apply {
        src = jsSrc
    }
    jsSetOnLoad(script) {
        val cbs = maplibreLoadCallbacks.toList()
        maplibreLoadCallbacks.clear()
        cbs.forEach { it() }
    }
    document.head?.appendChild(script)
}

private fun jsInjectCriticalCss() {
    js(
        "(function(){" +
            "var s = document.createElement('style');" +
            "s.textContent = " +
                "'.maplibregl-map{position:relative;overflow:hidden;}' +" +
                "'.maplibregl-canvas-container{position:absolute;left:0;top:0;width:100%;height:100%;}' +" +
                "'.maplibregl-canvas-container canvas{position:absolute;left:0;top:0;}' +" +
                "'.maplibregl-canvas{position:absolute;left:0;top:0;width:100%;height:100%;}';" +
            "document.head.appendChild(s);" +
        "})()"
    )
}

private fun jsMaplibreReady(): Boolean =
    js("(typeof maplibregl !== 'undefined')")

private fun jsSetOnLoad(el: HTMLScriptElement, cb: () -> Unit) {
    js("(el.onload = function() { cb(); })")
}

private fun jsCreateMap(containerId: String, styleUrl: String): JsAny =
    js(
        "(new maplibregl.Map({" +
            "container: containerId," +
            "style: styleUrl," +
            "center: [-0.1278, 51.5074]," +
            "zoom: 8" +
        "}))"
    )

private fun jsAttachLoadHandler(map: JsAny, cb: () -> Unit) {
    js(
        "(function(){" +
            "var setup = function(){" +
                "if (!map.getSource('h3-source')) {" +
                    "map.addSource('h3-source', { type: 'geojson', data: { type: 'FeatureCollection', features: [] } });" +
                    "map.addLayer({ id: 'h3-fill', type: 'fill', source: 'h3-source', paint: { 'fill-color': '#0066ff', 'fill-opacity': 0.25 } });" +
                    "map.addLayer({ id: 'h3-outline', type: 'line', source: 'h3-source', paint: { 'line-color': '#0066ff', 'line-width': 2 } });" +
                "}" +
                "cb();" +
            "};" +
            "if (map.loaded()) { setup(); } else { map.on('load', setup); }" +
        "})()"
    )
}

private fun jsSetSourceData(map: JsAny, geojson: String) {
    js("(function(){ var src = map.getSource('h3-source'); if (src) src.setData(JSON.parse(geojson)); })()")
}

private fun jsFitBounds(map: JsAny, west: Double, south: Double, east: Double, north: Double) {
    js("map.fitBounds([[west, south], [east, north]], { padding: 24, animate: true, duration: 300 })")
}

private fun jsResizeMap(map: JsAny) {
    js("map.resize()")
}

private fun jsObserveResize(div: HTMLDivElement, map: JsAny) {
    js("(function(){ map.__h3obs = new ResizeObserver(function(){ map.resize(); }); map.__h3obs.observe(div); })()")
}

private fun jsRemoveMap(map: JsAny) {
    js("map.remove()")
}

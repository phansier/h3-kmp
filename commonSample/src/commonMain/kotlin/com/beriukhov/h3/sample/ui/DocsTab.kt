package com.beriukhov.h3.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

private data class ApiEntry(
    val signature: String,
    val description: String,
    val example: String,
)

private val apiEntries = listOf(
    ApiEntry(
        signature = "H3.geoToH3(geo: LatLng, res: Int): ULong",
        description = "Returns the H3 cell index containing the given lat/lng at resolution `res` (0..15).",
        example = "val cell: ULong = H3.geoToH3(LatLng(51.5074, -0.1278), res = 9)",
    ),
    ApiEntry(
        signature = "H3.vertices(h3Index: String, decimalsToRound: Int = 8): List<LatLng>",
        description = "Returns the polygon boundary (vertices) of an H3 cell. Coordinates are rounded to `decimalsToRound` decimals.",
        example = "val ring: List<LatLng> = H3.vertices(\"084754a9ffffffff\")",
    ),
    ApiEntry(
        signature = "H3.h3ToString(h3: ULong): String",
        description = "Converts a ULong H3 index to its hexadecimal string form.",
        example = "val s: String = H3.h3ToString(0x084754a9ffffffffuL)",
    ),
    ApiEntry(
        signature = "H3.geoToH3String(geo: LatLng, res: Int): String",
        description = "Convenience: geoToH3 + h3ToString in one call.",
        example = "val s: String = H3.geoToH3String(LatLng(0.0, 0.0), res = 4) // \"084754a9ffffffff\"",
    ),
    ApiEntry(
        signature = "H3.polygon(h3Index: String): Polygon",
        description = "Wraps an H3 cell as a Polygon(h3Index, vertices).",
        example = "val p: Polygon = H3.polygon(\"084754a9ffffffff\")",
    ),
    ApiEntry(
        signature = "H3.polygons(h3Indexes: List<String>): List<Polygon>",
        description = "Batch version of polygon() for many indexes.",
        example = "val ps = H3.polygons(listOf(\"084754a9ffffffff\"))",
    ),
    ApiEntry(
        signature = "Double.roundToDecimals(decimals: Int = 8): Double",
        description = "Rounds a Double to the given number of decimals.",
        example = "val r: Double = 1.234567.roundToDecimals(2) // 1.23",
    ),
    ApiEntry(
        signature = "DEFAULT_DECIMALS_TO_ROUND = 8",
        description = "Default decimals used by vertices() / roundToDecimals().",
        example = "",
    ),
)

@Composable
fun DocsTab(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column {
                BasicText("H3-KMP", style = TypeHeadlineSmall)
                BasicText(
                    "Uber's H3 geo-index for Kotlin Multiplatform (Android, iOS, wasmJs).",
                    style = TypeBodyMedium,
                )
            }
        }
        items(apiEntries) { entry ->
            AppCard {
                Column(modifier = Modifier.padding(12.dp)) {
                    BasicText(
                        text = entry.signature,
                        style = TypeTitleSmall.copy(fontFamily = FontFamily.Monospace),
                    )
                    BasicText(
                        text = entry.description,
                        style = TypeBodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    if (entry.example.isNotEmpty()) {
                        BasicText(
                            text = entry.example,
                            style = TypeBodySmall.copy(fontFamily = FontFamily.Monospace),
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

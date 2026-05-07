package com.beriukhov.h3.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.beriukhov.h3.sample.MapDemoState
import com.beriukhov.h3.sample.map.H3MapView
import com.beriukhov.h3.sample.rememberMapDemoState

@Composable
fun MapTab(modifier: Modifier = Modifier) {
    val state = rememberMapDemoState()
    val scroll = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GeoToH3Card(state)
        H3ToVerticesCard(state)
        H3MapView(
            vertices = state.verticesForH3Input,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun GeoToH3Card(state: MapDemoState) {
    AppCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicText("geoToH3", style = TypeTitleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppOutlinedField(
                    value = state.latLngText,
                    onValueChange = { state.latLngText = it },
                    label = "LatLng",
                    isError = state.latLngText.isNotBlank() && state.parsedLat == null && state.parsedLng == null,
                    modifier = Modifier.weight(1f),
                )
                AppOutlinedField(
                    value = state.resText,
                    onValueChange = { state.resText = it },
                    label = "res (0..15)",
                    isError = state.resText.isNotBlank() && state.parsedRes == null,
                    modifier = Modifier.weight(0.6f),
                )
            }
            val result = state.geoToH3Result
            BasicText(
                text = "H3 index: ${result ?: "-"}",
                style = TypeBodyMedium.copy(fontFamily = FontFamily.Monospace),
            )
            AppButton(
                onClick = { state.copyGeneratedIndexToInput() },
                enabled = result != null,
            ) {
                BasicText(
                    text = "Use this index in (b)",
                    style = TypeBodyMedium.copy(color = AppColors.OnPrimary),
                )
            }
        }
    }
}

@Composable
private fun H3ToVerticesCard(state: MapDemoState) {
    AppCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicText("vertices", style = TypeTitleMedium)
            AppOutlinedField(
                value = state.h3InputText,
                onValueChange = { state.h3InputText = it },
                label = "H3 index (hex)",
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            val verts = state.verticesForH3Input
            if (verts.isEmpty()) {
                BasicText(
                    text = if (state.h3InputText.isBlank()) "Enter an index above." else "Invalid index.",
                    style = TypeBodySmall,
                )
            } else {
                BasicText(
                    text = "${verts.size} vertices:",
                    style = TypeBodySmall,
                )
                verts.forEachIndexed { i, v ->
                    BasicText(
                        text = "[$i] ${v.lat}, ${v.lng}",
                        style = TypeBodySmall.copy(fontFamily = FontFamily.Monospace),
                    )
                }
            }
        }
    }
}

package com.beriukhov.h3.sample.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.beriukhov.h3.LatLng

@Composable
expect fun H3MapView(
    vertices: List<LatLng>,
    modifier: Modifier = Modifier,
)

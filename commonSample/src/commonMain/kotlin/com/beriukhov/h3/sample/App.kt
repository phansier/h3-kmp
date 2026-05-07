package com.beriukhov.h3.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.beriukhov.h3.sample.ui.AppColors
import com.beriukhov.h3.sample.ui.DocsTab
import com.beriukhov.h3.sample.ui.MapTab
import com.beriukhov.h3.sample.ui.TypeTitleMedium

@Composable
@Preview
fun App() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Map", "Docs")

    Column(
        modifier = Modifier
            .background(Color.White)
            .systemBarsPadding()
            .fillMaxSize(),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { index, title ->
                val selected = selectedTab == index
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = index },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BasicText(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = title,
                        style = TypeTitleMedium.copy(
                            color = if (selected) AppColors.Primary else Color.DarkGray,
                            textAlign = TextAlign.Center,
                        ),
                    )
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(if (selected) AppColors.Primary else Color.Transparent),
                    )
                }
            }
        }
        when (selectedTab) {
            0 -> MapTab(modifier = Modifier.fillMaxSize())
            1 -> DocsTab(modifier = Modifier.fillMaxSize())
        }
    }
}

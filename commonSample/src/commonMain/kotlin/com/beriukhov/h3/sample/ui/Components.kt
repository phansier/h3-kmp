package com.beriukhov.h3.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AppColors {
    val Primary = Color(0xFF6650A4)
    val OnPrimary = Color(0xFFFFFFFF)
    val Surface = Color(0xFFEFEAF4)
    val OnSurface = Color(0xFF1C1B1F)
    val Outline = Color(0xFF79747E)
    val Error = Color(0xFFB3261E)
    val Disabled = Color(0xFFB0B0B0)
}

val TypeHeadlineSmall = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium, color = AppColors.OnSurface)
val TypeTitleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = AppColors.OnSurface)
val TypeTitleSmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppColors.OnSurface)
val TypeBodyMedium = TextStyle(fontSize = 14.sp, color = AppColors.OnSurface)
val TypeBodySmall = TextStyle(fontSize = 12.sp, color = AppColors.OnSurface)

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.Surface),
    ) {
        content()
    }
}

@Composable
fun AppButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (enabled) AppColors.Primary else AppColors.Disabled)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun AppOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    singleLine: Boolean = false,
) {
    val borderColor = if (isError) AppColors.Error else AppColors.Outline
    Column(modifier = modifier) {
        BasicText(text = label, style = TypeBodySmall.copy(color = borderColor))
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = TypeBodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

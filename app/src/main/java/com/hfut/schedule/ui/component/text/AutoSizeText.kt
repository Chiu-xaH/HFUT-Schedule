package com.hfut.schedule.ui.component.text

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun AutoSizeText(
    text: String,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    var fontSize by remember { mutableStateOf(12.sp) }

    LaunchedEffect(text, height) {
        val targetHeightPx = with(density) { height.toPx() }

        var low = 4f
        var high = 200f

        while (high - low > 0.5f) {
            val mid = (low + high) / 2
            val result = textMeasurer.measure(
                text = text,
                style = TextStyle(fontSize = mid.sp)
            )

            if (result.size.height > targetHeightPx) {
                high = mid
            } else {
                low = mid
            }
        }

        fontSize = low.sp
    }

    Text(
        text = text,
        fontSize = fontSize,
        maxLines = 1,
        fontWeight = FontWeight.Light,
        modifier = modifier.height(height)
    )
}
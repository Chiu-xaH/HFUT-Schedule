package com.hfut.schedule.ui.style.color

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle

@Composable
fun topBarTitleStyle(
    color : Color = MaterialTheme.colorScheme.surface,
    textStyle : TextStyle = LocalTextStyle.current
) = textStyle.copy(
    shadow = Shadow(
        color = color,
        offset = Offset(0f, 0f),
        blurRadius = 10f
    )
)
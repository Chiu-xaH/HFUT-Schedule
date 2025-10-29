package com.hfut.schedule.ui.screen.home.calendar.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xah.mirror.shader.GlassStyle
import com.xah.mirror.shader.glassLayer
import com.xah.mirror.util.ShaderState

fun Modifier.calendarSquareGlass(
    state : ShaderState,
    color : Color,
    enabled : Boolean,
) : Modifier =
    this.glassLayer(
        state,
        style = GlassStyle(
            blur = 3.5.dp ,
            border = 30f,
            dispersion = 0f,
            distortFactor = 0.1f,
            stretchFactor = 0.4f,
            overlayColor = color
        ),
        enabled = enabled
    )


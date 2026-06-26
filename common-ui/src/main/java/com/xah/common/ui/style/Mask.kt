package com.xah.common.ui.style

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color

fun Modifier.mask(
    color : Color,
    targetAlpha : Float,
    show : Boolean
) : Modifier = composed {
    val alpha by animateFloatAsState(
        if(show) targetAlpha else 0f
    )
    this.drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(color.copy(alpha = alpha))
        }
    }
}


fun Modifier.mask(
    color : Color,
    show: Boolean
) : Modifier = composed {
    val targetColor by animateColorAsState(
        if(show) color else Color.Transparent
    )
    mask(color = targetColor)
}

fun Modifier.mask(
    color : Color,
) : Modifier =
    this.drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(color)
        }
    }
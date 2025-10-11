package com.xah.transition.style

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

actual fun Modifier.scaleMirror(scale: Float): Modifier = this.graphicsLayer {
    scaleX = scale
    scaleY = scale
}
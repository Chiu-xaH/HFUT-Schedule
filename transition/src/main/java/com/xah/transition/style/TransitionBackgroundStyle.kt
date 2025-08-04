package com.xah.transition.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TransitionBackgroundStyle (
    var motionBlur : Boolean = true,
    var forceTransition : Boolean = false,
    var blurRadius : Dp = 20.dp,
    var backgroundColor : Color = Color.Black.copy(0.4f),
    var scaleValue : Float = 0.825f//0.825f
)
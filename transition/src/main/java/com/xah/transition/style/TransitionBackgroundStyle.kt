package com.xah.transition.style

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TransitionBackgroundStyle (
    var motionBlur : Boolean = true,
    var level : TransitionLevel = TransitionLevel.NONE,
    var blurRadius : Dp = 20.dp,
    var backgroundDark : Float = 0.4f,
    var scaleValue : Float = 0.875f//0.825f
)
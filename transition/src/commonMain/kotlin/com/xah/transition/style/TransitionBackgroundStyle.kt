package com.xah.transition.style

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class TransitionBackgroundStyle (
    var level : TransitionLevel = TransitionLevel.HIGH,
    val blurRadius : Dp = 20.dp,
    val backgroundDark : Float = 0.25f,//0.125f,
    val scale : Float = 0.875f,// 0.875 0.825f,
    val backgroundDarkDiffer : Float = 0.15f//0.1f,
)
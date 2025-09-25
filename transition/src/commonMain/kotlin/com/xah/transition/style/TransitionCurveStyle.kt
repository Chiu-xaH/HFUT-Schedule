package com.xah.transition.style

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi

// 动画曲线 boundsTransform决定运动曲线 speedMs决定时长
@OptIn(ExperimentalSharedTransitionApi::class)
data class TransitionCurveStyle(
    val speedMs : Int = 950 / 2,
    val dampingRatio : Float = 0.825f,
    val stiffness : Int = 200,
)

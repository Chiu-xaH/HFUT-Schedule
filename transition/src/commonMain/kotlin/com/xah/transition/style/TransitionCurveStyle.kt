package com.xah.transition.style

import androidx.compose.animation.ExperimentalSharedTransitionApi

// 动画曲线 boundsTransform决定运动曲线 speedMs决定时长
@OptIn(ExperimentalSharedTransitionApi::class)
data class TransitionCurveStyle(
    val speedMs : Int = 450,// 模拟动画时长 在初始化LocalShareScope前使用，初始化后 推荐使用其isTransitionActive控制动画
    val dampingRatio : Float = 0.825f,
    val stiffness : Int = 200,
)

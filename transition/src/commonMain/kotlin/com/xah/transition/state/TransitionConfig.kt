package com.xah.transition.state

import androidx.compose.animation.ExperimentalSharedTransitionApi
import com.xah.transition.style.TransitionCurveStyle

object TransitionConfig {
    // 是否使用透明背景 无需改
    var transplantBackground = false
    // 从此处修改动画曲线
    @OptIn(ExperimentalSharedTransitionApi::class)
    val curveStyle = TransitionCurveStyle()
}



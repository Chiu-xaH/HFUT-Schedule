package com.xah.transition.state

import androidx.compose.animation.ExperimentalSharedTransitionApi
import com.xah.transition.style.TransitionBackgroundStyle
import com.xah.transition.style.TransitionCurveStyle

object TransitionState {
    // 是否使用透明背景 无需改
    var transplantBackground = false
    // 从此处修改动画曲线
    @OptIn(ExperimentalSharedTransitionApi::class)
    val curveStyle = TransitionCurveStyle()
    // 从此处修改背景模糊/缩放情况
    val transitionBackgroundStyle = TransitionBackgroundStyle()
    // 应用启动开屏第一页的Route 不配置会导致第一次进入APP时需要等待speedMs
    var firstStartRoute : String? = "HOME"
    // 是否完成第一次启动 无需改
    var started = false
}
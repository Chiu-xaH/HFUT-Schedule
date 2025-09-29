package com.xah.transition.state

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import com.xah.transition.style.TransitionBackgroundStyle
import com.xah.transition.style.TransitionCurveStyle

object TransitionConfig {
    // 是否使用透明背景 无需改
    var transplantBackground = false
    // 从此处修改动画曲线
    @OptIn(ExperimentalSharedTransitionApi::class)
    val curveStyle = TransitionCurveStyle()
    // 从此处修改背景模糊/缩放情况
    val transitionBackgroundStyle = TransitionBackgroundStyle()
    val firstStartRoute : List<String> = listOf("HOME","UPDATE_SUCCESSFUL","USE_AGREEMENT")
    // 是否完成第一次启动 无需改
    // 第一次使用转场动画 需要预热 否则掉帧
    var firstUse = true
    // 打开后的界面显示动画 时长不要太长
    val enterShowTransition :  EnterTransition = fadeIn(animationSpec = tween(durationMillis = 175, easing = LinearOutSlowInEasing))
    // 状态
    var action: NavAction? = null
}


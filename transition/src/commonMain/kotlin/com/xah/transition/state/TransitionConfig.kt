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
    val useFade : Boolean = true
    var firstStartRoute : List<String> = listOf("HOME","UPDATE_SUCCESSFUL","USE_AGREEMENT")
    // 是否完成第一次启动 无需改
    // 第一次使用转场动画 需要预热 否则掉帧
    var firstUse = true
    var firstTransition = true
    // 打开后的界面显示动画 时长不要太长
    val enterShowTransition :  EnterTransition = fadeIn(animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing))
}
//
//object TransitionConfig {
//    /** 是否使用透明背景，可随时修改 */
//    var transplantBackground: Boolean = false
//    // 是否完成第一次启动 无需改
//    // 第一次使用转场动画 需要预热 否则掉帧
//    var firstUse: Boolean = true
//    var firstTransition: Boolean = true
//
//
//    /** 以下配置项仅能通过 init 设置 */
//    @OptIn(ExperimentalSharedTransitionApi::class)
//    lateinit var curveStyle: TransitionCurveStyle
//        private set
//
//    lateinit var transitionBackgroundStyle: TransitionBackgroundStyle
//        private set
//
//    var useFade: Boolean = true
//        private set
//
//    var firstStartRoute: List<String> = listOf("HOME", "UPDATE_SUCCESSFUL", "USE_AGREEMENT")
//        private set
//
//
//    /** 初始化接口，外部只允许在这里配置 */
//    fun init(block: Builder.() -> Unit) {
//        val builder = Builder().apply(block)
//        curveStyle = builder.curveStyle
//        transitionBackgroundStyle = builder.transitionBackgroundStyle
//        useFade = builder.useFade
//        firstStartRoute = builder.firstStartRoute
//    }
//
//    /** Builder DSL，保证外部只能在 init 中修改 */
//    class Builder {
//        @OptIn(ExperimentalSharedTransitionApi::class)
//        var curveStyle: TransitionCurveStyle = TransitionCurveStyle()
//        var transitionBackgroundStyle: TransitionBackgroundStyle = TransitionBackgroundStyle()
//        var useFade: Boolean = true
//        var firstStartRoute: List<String> = listOf("HOME", "UPDATE_SUCCESSFUL", "USE_AGREEMENT")
//    }
//}
//


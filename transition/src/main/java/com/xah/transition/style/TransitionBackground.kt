package com.xah.transition.style

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.xah.transition.state.TransitionState
import com.xah.transition.util.isCurrentRoute

@Composable
fun Modifier.transitionBackground(
    navHostController: NavHostController,
    route : String,
) : Modifier = with(TransitionState.transitionBackgroundStyle) {
    //👍 NONE
    if(level == TransitionLevel.NONE) {
        return this@transitionBackground
    }
    if(route == TransitionState.firstStartRoute && TransitionState.firstUse) {
        return this@transitionBackground
    }
    // 禁用刚冷启动第一个界面模糊缩放
    if(TransitionState.firstUse && TransitionState.firstTransition) {
        TransitionState.firstUse = false
        return this@transitionBackground
    } else if(TransitionState.firstTransition) {
        // 禁用刚冷启动第一次转场动画的增强效果
        TransitionState.firstTransition = false
        return this@transitionBackground
    }
    val transplantBackground = TransitionState.transplantBackground
    val isExpanded = !navHostController.isCurrentRoute(route)
    val speed = TransitionState.curveStyle.speedMs

    val backgroundColor by animateFloatAsState(
        targetValue = if(isExpanded) backgroundDark else 0f,
        animationSpec = tween(speed, easing = FastOutSlowInEasing),
    )
    // 蒙版 遮罩
    if(!transplantBackground && level.code >= TransitionLevel.LOW.code)
        Box(modifier = Modifier.fillMaxSize().background(
            Color.Black.copy( if(level.code >= TransitionLevel.MEDIUM.code) backgroundColor else backgroundColor*0.75f)
        ).zIndex(1f))

    //👍 LOW
    if(level == TransitionLevel.LOW) {
        return this@transitionBackground
    }

    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded && motionBlur) blurRadius else 0.dp, label = ""
        ,
        animationSpec = tween(speed*7/5, easing = FastOutSlowInEasing)

    )

    //👍 MEDIUM
    if(level == TransitionLevel.MEDIUM) {
        return this@transitionBackground.blur(blurSize)
    }
    val scale = animateFloatAsState( //.875f
        targetValue = if (isExpanded) scaleValue else 1f,
        animationSpec = tween(speed*4/3, easing = FastOutSlowInEasing)
    )

    //👍 HIGH
    return this@transitionBackground.blur(blurSize).scale(scale.value)
}
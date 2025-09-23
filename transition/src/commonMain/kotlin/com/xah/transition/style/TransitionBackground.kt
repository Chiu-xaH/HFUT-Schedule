package com.xah.transition.style

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xah.transition.state.TransitionState
import com.xah.transition.util.isCurrentRouteWithoutArgs

@Composable
fun Modifier.transitionBackground(
    navHostController: NavHostController,
    route : String,
) : Modifier = transitionSkip(route,transitionDefaultBackground(navHostController,route))


@Composable
fun Modifier.transitionDefaultBackground(
    navHostController: NavHostController,
    route : String,
) : Modifier = with(TransitionState.transitionBackgroundStyle) {
    val transplantBackground = TransitionState.transplantBackground
    val isExpanded = !navHostController.isCurrentRouteWithoutArgs(route)
    val speed = TransitionState.curveStyle.speedMs

    val backgroundColor by animateFloatAsState(
        targetValue = if(isExpanded) {
            if(level.code == TransitionLevel.HIGH.code) backgroundDark else backgroundDark+backgroundDarkDiffer
        } else 0f,
        animationSpec = tween(speed, easing = FastOutSlowInEasing),
    )
    // 蒙版 遮罩
    val darkModifier = this@transitionDefaultBackground.let {
        if(!transplantBackground && level.code >= TransitionLevel.LOW.code) {
            it.drawWithContent {
                drawContent()
                drawRect(Color.Black.copy(alpha = backgroundColor))
            }
        } else it
    }

    //👍 LOW
    if(level == TransitionLevel.LOW) {
        return darkModifier
    }


    val scale = animateFloatAsState( //.875f
        targetValue = if (isExpanded) {
            scale
        } else 1f,
        animationSpec = tween(speed*4/3, easing = FastOutSlowInEasing)
    )
    //👍 MEDIUM
    if(level == TransitionLevel.MEDIUM) {
        return darkModifier.scale(scale.value)
    }

    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded) {
            blurRadius
        } else 0.dp,
        label = "",
        animationSpec = tween(speed*7/5, easing = FastOutSlowInEasing)
    )

    //👍 HIGH
    return darkModifier.blur(blurSize).scale(scale.value)
}



fun Modifier.transitionSkip(
    route : String,
    background : Modifier
): Modifier = with(TransitionState.transitionBackgroundStyle) {
    //👍 NONE
    if(level == TransitionLevel.NONE) {
        return this@transitionSkip
    }
    if(route in TransitionState.firstStartRoute && TransitionState.firstUse) {
        return this@transitionSkip
    }
    // 禁用刚冷启动第一个界面模糊缩放
    if(TransitionState.firstUse && TransitionState.firstTransition) {
        TransitionState.firstUse = false
        return this@transitionSkip
    } else if(TransitionState.firstTransition) {
        // 禁用刚冷启动第一次转场动画的增强效果
        TransitionState.firstTransition = false
        return this@transitionSkip
    }
    return background
}
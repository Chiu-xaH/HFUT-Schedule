package com.xah.transition.style

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.transitionDefaultBackground
import com.xah.transition.util.isCurrentRouteWithoutArgs

@Composable
fun Modifier.transitionBackground(
    navHostController: NavHostController,
    route : String,
) : Modifier = transitionSkip(transitionDefaultBackground(navHostController,route))


@Composable
fun Modifier.transitionDefaultBackground(
    navHostController: NavHostController,
    route : String,
) : Modifier = with(TransitionConfig.transitionBackgroundStyle) {
    val transplantBackground = TransitionConfig.transplantBackground
    val isExpanded = !navHostController.isCurrentRouteWithoutArgs(route)
    val speed = TransitionConfig.curveStyle.speedMs

    val backgroundColor by animateFloatAsState(
        targetValue = if(isExpanded) {
            if(level == TransitionLevel.HIGH)
                backgroundDark*2/3
            else
                backgroundDark
        } else 0f,
        animationSpec = tween(speed, easing = FastOutSlowInEasing),
    )
    // 蒙版 遮罩
    val darkModifier = this@transitionDefaultBackground
        .let {
        if(!transplantBackground && level.code >= TransitionLevel.LOW.code) {
            it.drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(Color.Black.copy(alpha = backgroundColor))
                }
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
        animationSpec = tween(speed*7/5, easing = FastOutSlowInEasing)
    )
    //👍 MEDIUM
    if(level == TransitionLevel.MEDIUM) {
        return darkModifier.scaleMirror(scale.value)
//            .graphicsLayer {
//            scaleX = scale.value
//            scaleY = scale.value
//        }
    }

    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded) {
            blurRadius
        } else 0.dp,
        label = "",
        animationSpec = tween(speed*6/5, easing = FastOutSlowInEasing)
    )

    //👍 HIGH
    return darkModifier
        .blur(blurSize)
        .scaleMirror(scale.value)
//        .graphicsLayer {
//            scaleX = scale.value
//            scaleY = scale.value
//        }
}



fun Modifier.transitionSkip(
    background : Modifier
): Modifier = with(TransitionConfig.transitionBackgroundStyle) {
    //👍 NONE
    if(level <= TransitionLevel.NONE) {
        return this@transitionSkip
    }
    if(TransitionConfig.firstUse) {
        // 第一次动画
        return this@transitionSkip
    }
    return background
}



package com.xah.transition.style

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.xah.transition.util.currentRoute
import com.xah.transition.state.TransitionState
import com.xah.transition.util.isCurrentRoute
import kotlinx.coroutines.delay

@Composable
fun Modifier.transitionBackground(
    navHostController: NavHostController,
    route : String,
) : Modifier = with(TransitionState.transitionBackgroundStyle) {
    if(TransitionState.firstStartRoute == route && !TransitionState.started) {
        TransitionState.started = true
        return Modifier
    }
    val transplantBackground = TransitionState.transplantBackground
    val isExpanded = !navHostController.isCurrentRoute(route)
    val speed = TransitionState.curveStyle.speedMs + TransitionState.curveStyle.speedMs/2
    // 稍微晚于运动结束
    val blurSize by animateDpAsState(
        targetValue = if (isExpanded && motionBlur) blurRadius else 0.dp, label = ""
        ,animationSpec = tween(speed, easing = FastOutSlowInEasing),
    )
    val scale = animateFloatAsState( //.875f
        targetValue = if (isExpanded) scaleValue else 1f,
        animationSpec = tween(speed , easing = FastOutSlowInEasing)
    )
    val backgroundColor by animateColorAsState(
        targetValue = if(isExpanded) backgroundColor else Color.Transparent,
        animationSpec = tween(TransitionState.curveStyle.speedMs, easing = FastOutSlowInEasing)
    )
    // 蒙版 遮罩
    if(!transplantBackground)
        Box(modifier = Modifier.fillMaxSize().background(backgroundColor).zIndex(2f))

    val transitionModifier = if(forceTransition) this@transitionBackground.scale(scale.value).blur(blurSize) else this@transitionBackground

    transitionModifier
}
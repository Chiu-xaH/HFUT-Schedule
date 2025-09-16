package com.xah.transition.util

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigationevent.NavigationEventHandler
import androidx.navigationevent.NavigationEventInfo
import com.xah.transition.style.DefaultTransitionStyle
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

private const val targetScale = 0.075f
@Composable
fun TransitionPredictiveBackHandler(
    navController : NavHostController,
    onScale: (Float) -> Unit
) {
    PredictiveBackHandler(navController.allRouteStack().size > 1) { progress: Flow<BackEventCompat> ->
        // code for gesture back started
        try {
            progress.collect { backEvent ->
                // code for progress
                onScale(1f - (targetScale * backEvent.progress))
            }
            // code for completion
            onScale(0f)
            navController.popBackStack()
        } catch (e: CancellationException) {
            // code for cancellation
            onScale(1f)
        }
    }
}

@Composable
fun TransitionPredictiveBackHandler(
    navController: NavHostController,
    scale: Animatable<Float, AnimationVector1D>
) {
    // 用法 val scale = remember { Animatable(1f) }
    PredictiveBackHandler(navController.allRouteStack().size > 1) { progress: Flow<BackEventCompat> ->
        try {
            progress.collect { backEvent ->
                val targetScale = 1f - (targetScale * backEvent.progress)
                scale.snapTo(targetScale)
            }
            // 手势完成，动画回到 1f，再返回上一页
            scale.snapTo(0f)
//            scale.animateTo(1f, tween(duration.toInt()))
            navController.popBackStack()
        } catch (e: CancellationException) {
            // 手势取消，动画回到 1f
            scale.animateTo(1f)
        }
    }
}

data class MyScreenInfo(val screenName: String) : NavigationEventInfo


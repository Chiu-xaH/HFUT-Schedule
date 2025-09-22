package com.xah.transition.component

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.TransitionState

// 容器共享元素 如果两个容器颜色不同，可以启用渐变fade参数
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.containerShare(
    route : String,
    roundShape : Shape = MaterialTheme.shapes.small,
) : Modifier  {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current
    with(sharedTransitionScope) {
        val isAnimating = this.isTransitionActive
        val state = rememberSharedContentState(key = "container_$route")
        val transition = spring(
            dampingRatio = TransitionState.curveStyle.dampingRatio,
            stiffness = TransitionState.curveStyle.stiffness.toFloat(),
            visibilityThreshold = Rect.VisibilityThreshold
        )
        val boundsTransform = BoundsTransform { _,_ ->
            transition
        }
        return this@containerShare
            .let {
                if(TransitionState.useFade) {
                    it.sharedBounds(
                        boundsTransform = boundsTransform,
                        sharedContentState = state,
                        animatedVisibilityScope = animatedContentScope,
                        enter = fadeIn(animationSpec = tween(durationMillis = TransitionState.curveStyle.speedMs)),
                        exit = fadeOut(animationSpec = tween(durationMillis = TransitionState.curveStyle.speedMs)),
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    )
                } else {
                    it.sharedElement(
                        boundsTransform = boundsTransform,
                        sharedContentState = state,
                        animatedVisibilityScope = animatedContentScope,
                    )
                }
            }
            .let { if(isAnimating) it.clip(roundShape) else it }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.singleElementShare(
    title : String,
    route : String,
) : Modifier {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedContentScope = LocalAnimatedContentScope.current
    return with(sharedTransitionScope) {
        this@singleElementShare.sharedElement(
            boundsTransform = BoundsTransform { _,_ ->
                spring(
                    dampingRatio = TransitionState.curveStyle.dampingRatio,
                    stiffness = TransitionState.curveStyle.stiffness.toFloat(),
                    visibilityThreshold = Rect.VisibilityThreshold
                )
            },
            sharedContentState = rememberSharedContentState(key = "${title}_$route"),
            animatedVisibilityScope = animatedContentScope,
        )
    }
}
// 标题共享元素
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.iconElementShare(
    route : String,
) : Modifier = singleElementShare("icon",route)
// 图标共享元素
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.titleElementShare(
    route : String,
) : Modifier = singleElementShare("title",route)
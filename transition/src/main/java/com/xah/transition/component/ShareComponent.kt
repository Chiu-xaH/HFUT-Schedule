package com.xah.transition.component

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.ScaleToBounds
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.xah.transition.state.TransitionState

// 容器共享元素 注意：主界面（TransitionScaffold）需要指定resize=false(已经在CustomScaffold指定了)，初始容器无需指定
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.containerShare(
    modifier: Modifier = Modifier,
    animatedContentScope: AnimatedContentScope,
    route : String,
    resize : Boolean = true,
    roundShape : Shape = MaterialTheme.shapes.small,
) : Modifier {
//    val tilt = animatedContentScope.transition.animateFloat (
//        transitionSpec = { spring() },
//        label = "tilt"
//    ){ state ->
//        if (state == EnterExitState.Visible) {
//            0f
//        } else {
//            -20f
//        }
//    }

    return modifier
        .sharedBounds(
        boundsTransform = BoundsTransform { _,_ ->
            spring(
                dampingRatio = TransitionState.curveStyle.dampingRatio,
                stiffness = TransitionState.curveStyle.stiffness.toFloat(),
                visibilityThreshold = Rect.VisibilityThreshold
            )
        },
        enter = fadeIn(animationSpec = tween(durationMillis = TransitionState.curveStyle.speedMs),),
        exit = fadeOut(animationSpec = tween(durationMillis = TransitionState.curveStyle.speedMs),),
        sharedContentState = rememberSharedContentState(key = "container_$route"),
        animatedVisibilityScope = animatedContentScope,
        resizeMode = if(resize) SharedTransitionScope.ResizeMode.RemeasureToBounds else ScaleToBounds(ContentScale.FillWidth, Center)
    )
        .let {
            if(false) it.graphicsLayer {
//                rotationX = tilt.value
                cameraDistance = 12 * density // 防止透视太夸张
                transformOrigin = TransformOrigin(0.5f, 0f) // 从顶部开始旋转
            } else it
        }
        .clip(roundShape)

}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.singleElementShare(
    modifier: Modifier = Modifier,
    animatedContentScope: AnimatedContentScope,
    title : String,
    route : String,
) : Modifier {
    return modifier.sharedElement(
        boundsTransform = BoundsTransform { _,_ ->
            spring(
                dampingRatio = TransitionState.curveStyle.dampingRatio,
                stiffness = TransitionState.curveStyle.stiffness.toFloat(),
                visibilityThreshold = Rect.VisibilityThreshold
            )
        },
        state = rememberSharedContentState(key = "${title}_$route"),
        animatedVisibilityScope = animatedContentScope,
    )
}
// 标题共享元素
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.iconElementShare(
    modifier: Modifier = Modifier,
    animatedContentScope: AnimatedContentScope,
    route : String,
) : Modifier = singleElementShare(modifier,animatedContentScope,"icon",route)
// 图标共享元素
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.titleElementShare(
    modifier: Modifier = Modifier,
    animatedContentScope: AnimatedContentScope,
    route : String,
) : Modifier = singleElementShare(modifier,animatedContentScope,"title",route)
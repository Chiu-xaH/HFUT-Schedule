package com.hfut.schedule.ui.component.container

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.ui.util.navigation.AppAnimationManager

// 容器共享切换
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun <S> ShareContainer2D(modifier: Modifier = Modifier,label: S, contents : Map<S,(@Composable () -> Unit)>) {
    SharedTransitionLayout(modifier = modifier) {
        AnimatedContent(
            targetState = label,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED)) togetherWith fadeOut(animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED*2))
            },
            label = ""
        ) { targetLabel ->
            Box(
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "container"),
                    animatedVisibilityScope = this,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
            ) {
                contents[targetLabel]?.invoke()
            }
        }
    }
}
// 两个容器共享切换 show==true时显示
@Composable
fun ShareTwoContainer2D(modifier: Modifier = Modifier, show : Boolean, defaultContent : @Composable ()-> Unit, secondContent : @Composable ()-> Unit) = ShareContainer2D(modifier = modifier, label = show, contents = mapOf(false to defaultContent,true to secondContent))
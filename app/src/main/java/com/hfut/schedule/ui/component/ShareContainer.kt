package com.hfut.schedule.ui.component

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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ui.util.MyAnimationManager

// 容器共享切换
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun <S> ShareContainer(modifier: Modifier = Modifier,label: S,contents : Map<S,(@Composable () -> Unit)>) {
    SharedTransitionLayout(modifier = modifier) {
        AnimatedContent(
            targetState = label,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = MyAnimationManager.ANIMATION_SPEED)) togetherWith fadeOut(animationSpec = tween(durationMillis = MyAnimationManager.ANIMATION_SPEED*2))
            },
            label = ""
        ) { targetLabel ->
//            // 判断动画是否正在进行
//            val isTransitionRunning = transition.currentState != transition.targetState
//
//            // 只有在动画中应用模糊
//            val blurSize by animateDpAsState(
//                targetValue = if (isTransitionRunning) 10.dp else 0.dp,
//                animationSpec = tween(MyAnimationManager.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
//                label = "blurDuringTransition"
//            )

            Box(
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "container"),
                    animatedVisibilityScope = this,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                )
//                    .blur(blurSize),
            ) {
                contents[targetLabel]?.invoke()
            }
        }
    }
}
// 两个容器共享切换 show==true时显示
@Composable
fun ShareTwoContainer(modifier: Modifier = Modifier,show : Boolean,defaultContent : @Composable ()-> Unit,secondContent : @Composable ()-> Unit) = ShareContainer(modifier = modifier, label = show, contents = mapOf(false to defaultContent,true to secondContent))
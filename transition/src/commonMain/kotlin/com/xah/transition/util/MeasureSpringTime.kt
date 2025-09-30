package com.xah.transition.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xah.transition.state.TransitionConfig
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
// 测量动画时长 TransitionCurveStyle.speedMs
@OptIn(ExperimentalTime::class)
@Composable
fun SpeedTest(
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = TransitionConfig.curveStyle.dampingRatio,
        stiffness = TransitionConfig.curveStyle.stiffness.toFloat()
    )
) {
    val anim = remember { Animatable(0f) }
    var result: Long by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                scope.launch {
                    // 复位
                    anim.animateTo(0f)
                    // 测量
                    val start = Clock.System.now().toEpochMilliseconds()
                    anim.animateTo(
                        targetValue = 1f,
                        animationSpec = animationSpec
                    )
                    val end = Clock.System.now().toEpochMilliseconds()
                    result = end-start
                }
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("测量 $result ms")
        }
    }
}
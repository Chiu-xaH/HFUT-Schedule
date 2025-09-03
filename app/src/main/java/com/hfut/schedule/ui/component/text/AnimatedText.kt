package com.hfut.schedule.ui.component.text

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    delayMillisPerChar: Int = 200 // 每个字延迟时间
) {
    Row(modifier) {
        text.forEachIndexed { index, char ->
            // 控制是否显示
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(index * delayMillisPerChar.toLong())
                visible = true
            }

            AnimatedVisibility(visible = visible) {
                // 出现时从小到大
                var scale by remember { mutableStateOf(0.6f) }
                val animatedScale by animateFloatAsState(
                    targetValue = scale,
                    animationSpec = tween(durationMillis = 400, easing = overshootInterpolatorEasing(2f)),
                    label = "scaleAnim"
                )
                // 动画启动
                LaunchedEffect(visible) {
                    if (visible) scale = 1f
                }

                Text(
                    text = char.toString(),
                    modifier = Modifier.graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    },
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
// 自定义 Easing，Overshoot 效果更像“弹出”
private fun overshootInterpolatorEasing(tension: Float) = Easing { fraction ->
    (fraction - 1).let { it * it * ((tension + 1) * it + tension) + 1 }
}

enum class Phase { SHOW, HOLD, HIDE }

@Composable
fun AnimatedTextCarousel(
    texts: List<String>,
    modifier: Modifier = Modifier,
    delayMillisPerChar: Int = 100,     // 每个字出现/消失间隔
    holdMillis: Int = 1000,            // 每段停留时长
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
) {
    if (texts.isEmpty()) return

    var currentIndex by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(Phase.SHOW) }

    // 用不可变 List 存状态（也可以用 mutableStateListOf）
    var visibleFlags by remember { mutableStateOf(List(texts[0].length) { false }) }

    val currentText = texts[currentIndex]

    // 确保在 index 切换时，visibleFlags 至少被重置为正确长度（尽量减少越界窗口）
    LaunchedEffect(currentIndex) {
        visibleFlags = List(currentText.length) { false }
    }

    // 动画控制器
    LaunchedEffect(currentIndex, phase) {
        when (phase) {
            Phase.SHOW -> {
                // 再次保证重置（防止竞争）
                visibleFlags = List(currentText.length) { false }
                for (i in currentText.indices) {
                    delay(delayMillisPerChar.toLong())
                    visibleFlags = visibleFlags.toMutableList().also { it[i] = true }
                }
                phase = Phase.HOLD
            }
            Phase.HOLD -> {
                delay(holdMillis.toLong())
                phase = Phase.HIDE
            }
            Phase.HIDE -> {
                for (i in currentText.indices.reversed()) {
                    delay(delayMillisPerChar.toLong())
                    visibleFlags = visibleFlags.toMutableList().also { it[i] = false }
                }
                // 全部缩回后，停留一会儿
                delay(holdMillis.toLong())
                currentIndex = (currentIndex + 1) % texts.size
                phase = Phase.SHOW
            }
        }
    }

    // UI：在取 visibleFlags 时做安全取值，避免 OOB
    Row(modifier) {
        key(currentIndex) { // 可选：强制在 index 切换时重建子树，减少状态残留问题
            currentText.forEachIndexed { index, char ->
                val isVisible = visibleFlags.getOrNull(index) ?: false
                Box {
                    // 1) 占位文本，始终存在且不可见（占用真实字符宽度）
                    Text(
                        text = "",
                        style = textStyle
                    )

                    Row {
                        AnimatedVisibility(
                            visible = isVisible,
                        ) {
                            val animatedScale by animateFloatAsState(
                                targetValue = if (isVisible) 1f else 0.6f,
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = overshootInterpolatorEasing(2f)
                                )
                            )

                            Text(
                                text = char.toString(),
                                modifier = Modifier.graphicsLayer {
                                    scaleX = animatedScale
                                    scaleY = animatedScale
                                },
                                style = textStyle
                            )
                        }
                    }
                }
            }
        }
    }
}

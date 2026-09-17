package com.xah.common.ui.style.color

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color


enum class ProgressDirection {
    LEFT_TO_RIGHT,
    BOTTOM_TO_TOP
}

@Composable
fun Modifier.progressEffect(
    progress: Double,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    direction: ProgressDirection = ProgressDirection.LEFT_TO_RIGHT,
    animate: Boolean = true
) = this.progressEffect(
    progress.toFloat(),
    color,
    direction,
    animate
)

@Composable
fun Modifier.progressEffect(
    progress: Float,
    color: Color = MaterialTheme.colorScheme.primaryContainer.copy(.5f),
    direction: ProgressDirection = ProgressDirection.LEFT_TO_RIGHT,
    animate: Boolean = true
): Modifier {
    val safeProgress = progress.coerceIn(0f, 1f)

    // 首次进入从0开始增长到目标进度，progress变化时从当前值动画到新值
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(safeProgress, animate) {
        if (animate) {
            animatedProgress.animateTo(
                targetValue = safeProgress,
                animationSpec = tween(durationMillis = 800)
            )
        } else {
            animatedProgress.snapTo(safeProgress)
        }
    }

    return this.drawBehind {
        if (animatedProgress.value <= 0f) {
            return@drawBehind
        }
        val w = size.width
        val h = size.height
        if (w == 0f || h == 0f) {
            return@drawBehind
        }
        val rect = when (direction) {
            ProgressDirection.LEFT_TO_RIGHT -> {
                Rect(Offset.Zero, Size(w * animatedProgress.value, h))
            }
            ProgressDirection.BOTTOM_TO_TOP -> {
                Rect(
                    offset = Offset(0f, h * (1f - animatedProgress.value)),
                    size = Size(w, h * animatedProgress.value)
                )
            }
        }
        drawRect(
            color = color,
            topLeft = rect.topLeft,
            size = rect.size
        )
    }
}

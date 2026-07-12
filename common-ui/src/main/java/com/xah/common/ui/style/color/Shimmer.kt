package com.xah.common.ui.style.color

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.hypot


enum class ShimmerAngle(val angle : Float) {
    START_TO_END(0F),START_TOP_TO_BOTTOM_END(45F)
}

/**
 * 从左到右，高光扫描效果，记得裁切，否则是直角矩形
 */
@Composable
fun Modifier.shimmerEffect(
    angle: ShimmerAngle = ShimmerAngle.START_TOP_TO_BOTTOM_END,
    color : Color =  MaterialTheme.colorScheme.surface.copy(0.55f),
    durationMills : Int = 1000
): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMills, easing = LinearEasing, delayMillis = durationMills),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer-progress"
    )

    return this.drawWithContent {
        drawContent()

        val w = size.width
        val h = size.height
        if (w == 0f || h == 0f) return@drawWithContent

        // 计算对角线长度，保证旋转后能覆盖全区域
        val dig = hypot(w, h)

        withTransform({
            rotate(angle.angle, pivot = Offset(w / 2, h / 2))
            translate(left = progress * dig)
        }) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, color, Color.Transparent)
                ),
                topLeft = Offset(-dig, -h),
                size = Size(dig * 2, h * 3) // 足够大避免漏边
            )
        }
    }
}


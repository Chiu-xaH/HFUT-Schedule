package com.hfut.schedule.ui.component.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource

val rainbowBrush = Brush.sweepGradient(
    colors = listOf(
        Color(0xFFE53935), // 红
        Color(0xFFFB8C00), // 橙
        Color(0xFFFDD835), // 黄
        Color(0xFF43A047), // 绿
        Color(0xFF1E88E5), // 蓝
        Color(0xFF8E24AA), // 紫
        Color(0xFFE53935), // 闭环
    )
)

@Composable
fun BrushIcon(
    icon: Int,
    modifier: Modifier = Modifier,
    brush : Brush = rainbowBrush,
    angle : Float = 90f
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = null,
        tint = Color.White,
        modifier = modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    rotate(
                        degrees = angle,
                        pivot = center
                    ) {
                        drawRect(
                            brush = brush,
                            blendMode = BlendMode.SrcIn
                        )
                    }
                }
            }
    )
}



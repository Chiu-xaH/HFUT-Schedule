package com.hfut.schedule.ui.component.chart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

//成绩雷达图
data class RadarData(val label: String, val value: Float)

@Composable
fun RadarChart(data: List<RadarData>, modifier: Modifier = Modifier) {
    val maxValue = 1f
//        data.maxOf { it.value }
    val animatedValues = remember { data.map { Animatable(0f) } }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        animatedValues.forEachIndexed { index, animatable ->
            coroutineScope.launch {
                animatable.animateTo(
                    targetValue = data[index].value,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    val angle = 360f / data.size
    val startAngle = -90f // 使第一个点位于顶部
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.inversePrimary
    val fillColor = primaryColor.copy(alpha = 0.3f)

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // 绘制外边框
        val outerPath = Path().apply {
            data.forEachIndexed { index, _ ->
                val theta = Math.toRadians((startAngle + angle * index).toDouble()).toFloat()
                val x = center.x + radius * cos(theta)
                val y = center.y + radius * sin(theta)
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(outerPath, color = onPrimaryColor, style = Stroke(width = 2.dp.toPx()))

        // 绘制轴线
        data.forEachIndexed { index, radarData ->
            val theta = Math.toRadians((startAngle + angle * index).toDouble()).toFloat()
            val x = center.x + radius * cos(theta)
            val y = center.y + radius * sin(theta)
            drawLine(onPrimaryColor, start = center, end = Offset(x, y), strokeWidth = 1.dp.toPx())

            // 绘制标签
            val labelX = center.x + (radius + 40.dp.toPx()) * cos(theta)
            val labelY = center.y + (radius + 20.dp.toPx()) * sin(theta)
            drawContext.canvas.nativeCanvas.drawText(
                radarData.label,
                labelX,
                labelY,
                android.graphics.Paint().apply {
                    color = primaryColor.toArgb()
                    textSize = 14.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // 绘制多边形
        val path = Path().apply {
            animatedValues.forEachIndexed { index, animatable ->
                val theta = Math.toRadians((startAngle + angle * index).toDouble()).toFloat()
                val x = center.x + (animatable.value / maxValue) * radius * cos(theta)
                val y = center.y + (animatable.value / maxValue) * radius * sin(theta)
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path, color = fillColor) // 填充颜色
        drawPath(path, color = primaryColor, style = Stroke(width = 2.dp.toPx()))
    }
}

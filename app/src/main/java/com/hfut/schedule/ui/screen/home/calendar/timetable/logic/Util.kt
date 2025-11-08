package com.hfut.schedule.ui.screen.home.calendar.timetable.logic

import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun Modifier.drawLineTimeTable(
    columnCount : Float,
    width : Dp = 1.dp,
    color : Color = DividerDefaults.color
): Modifier {
    return this.drawBehind {
        val w = size.width
        val h = size.height
        // 虚线在列边界
        for (i in 0..columnCount.toInt()) {
            val x = w * i / columnCount.toFloat()
            drawLine(
                color = color,
                strokeWidth = width.toPx(),
                start = Offset(x, 0f),
                end = Offset(x, h),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
    }
}

fun parseTimeToFloat(time: String): Float {
    val parts = time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return hour + minute / 60f
}

// 早八
const val DEFAULT_START_TIME = 8f
const val DEFAULT_END_TIME = 24f

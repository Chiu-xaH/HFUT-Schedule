package com.hfut.schedule.ui.screen.home.calendar.timetable.logic

import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

@Composable
fun Modifier.drawCurrentTimeLine(
    hourPx: Float,
    startTime: Float,
    endTime: Float,
    zipTime: List<Pair<Float, Float>>,
    zipTimeFactor: Float,
    color: Color = Color.Red,
    lineWidth: Dp = 1.5.dp,
    dotRadius: Dp = 3.dp
): Modifier {
    val currentTime = remember {
        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        hour + minute / 60f
    }

    return if (currentTime < startTime || currentTime > endTime) {
        this
    } else {
        this.drawBehind {
            val y = timeToY(currentTime, hourPx, startTime, zipTime, zipTimeFactor)
            val w = size.width

            drawLine(
                color = color,
                strokeWidth = lineWidth.toPx(),
                start = Offset(0f, y),
                end = Offset(w, y)
            )

            drawCircle(
                color = color,
                radius = dotRadius.toPx(),
                center = Offset(0f, y)
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
const val DEFAULT_START_TIME = "08:00"
const val DEFAULT_END_TIME = "22:00"
const val MOON_REST_START_TIME = "12:10"
const val MOON_REST_END_TIME = "14:00"

fun timeToY(
    hour: Float,
    hourPx: Float,
    startTime: Float,
    compressList: List<Pair<Float, Float>>,
    compressFactor: Float
): Float {
    // 累积时间段位移
    var offset = 0f
    var lastEnd = startTime

    for ((compressStart, compressEnd) in compressList.sortedBy { it.first }) {
        when {
            hour <= compressStart -> {
                // 当前时间在压缩段前
                return offset + (hour - lastEnd) * hourPx
            }
            hour in compressStart..compressEnd -> {
                // 当前时间在压缩段内部
                val before = offset + (compressStart - lastEnd) * hourPx
                val inner = (hour - compressStart) * hourPx * compressFactor
                return before + inner
            }
            else -> {
                // 当前时间在压缩段之后
                offset += (compressStart - lastEnd) * hourPx
                offset += (compressEnd - compressStart) * hourPx * compressFactor
                lastEnd = compressEnd
            }
        }
    }

    // 超过所有压缩段的情况
    return offset + (hour - lastEnd) * hourPx
}

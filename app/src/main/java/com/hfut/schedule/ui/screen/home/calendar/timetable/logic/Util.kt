package com.hfut.schedule.ui.screen.home.calendar.timetable.logic

import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import kotlinx.coroutines.delay
import java.util.Calendar


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
    todayColumnIndex: Int? = null,
    columnWidthPx: Float = 0f,
): Modifier {
    val enableShowCalendarTimeLine by DataStoreManager.enableShowCalendarTimeLine.collectAsState(initial = false)
    if(!enableShowCalendarTimeLine) {
        return this
    }

    // 非本周不画线
    if (todayColumnIndex == null) {
        return this
    }

    val color = remember { Color.Red }
    val lineWidth = remember { 1.5.dp }
    val dotRadius = lineWidth*2

    var currentTime by remember { mutableFloatStateOf(currentTimeToFloat()) }

    // 分钟刷新
    LaunchedEffect(Unit) {
        while (true) {
            val now = Calendar.getInstance()
            val second = now.get(Calendar.SECOND)
            val millis = now.get(Calendar.MILLISECOND)
            delay((60 - second) * 1000L - millis)
            currentTime = currentTimeToFloat()
        }
    }

    return if (currentTime < startTime || currentTime > endTime) {
        this
    } else {
        this.drawWithContent {
            val y = timeToY(currentTime, hourPx, startTime, zipTime, zipTimeFactor)
            val w = size.width

            drawLine(
                color = color,
                strokeWidth = lineWidth.toPx(),
                start = Offset(0f, y),
                end = Offset(w, y)
            )

            drawContent()

            if (columnWidthPx > 0f) {
                val left = todayColumnIndex * columnWidthPx
                val right = left + columnWidthPx
                drawLine(
                    color = color,
                    strokeWidth = lineWidth.toPx(),
                    start = Offset(left, y),
                    end = Offset(right, y)
                )
//                drawCircle(
//                    color = color,
//                    radius = dotRadius.toPx(),
//                    center = Offset(left, y)
//                )
            }
        }
    }
}

private fun currentTimeToFloat(): Float {
    val now = Calendar.getInstance()
    val hour = now.get(Calendar.HOUR_OF_DAY)
    val minute = now.get(Calendar.MINUTE)
    return hour + minute / 60f
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

package com.hfut.schedule.ui.screen.home.calendar.timetable


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.style.padding.InnerPaddingHeight
import kotlin.math.roundToInt


private data class PositionedSquare(
    val course: TimeTableItem,
    val start: Float,
    val end: Float,
    val columnIndex: Int,
    val totalColumns: Int
)

private fun layoutSquaresForDay(items: List<TimeTableItem>): List<PositionedSquare> {
    if (items.isEmpty()) return emptyList()
    val sorted = items.sortedBy { parseTimeToFloat(it.startTime) }

    val positioned = mutableListOf<PositionedSquare>()
    val active = mutableListOf<PositionedSquare>()

    for (course in sorted) {
        val start = parseTimeToFloat(course.startTime)
        val end = parseTimeToFloat(course.endTime)

        // 移除已结束的课程
        active.removeAll { it.end <= start }

        val columnIndex = active.size
        val totalColumns = active.size + 1

        val pc = PositionedSquare(course, start, end, columnIndex, totalColumns)
        active.add(pc)
        positioned.add(pc)
    }

    // 修正 totalColumns，避免 maxOf 空集异常
    return positioned.map { p ->
        val overlapping = positioned.filter {
            it.course.dayOfWeek == p.course.dayOfWeek &&
                    it.start < p.end && it.end > p.start
        }
        p.copy(totalColumns = if (overlapping.isNotEmpty()) overlapping.maxOf { it.columnIndex + 1 } else 1)
    }
}


fun timeToY(
    hour: Float,
    hourPx: Float,
    startHour: Int,
    compressList: List<Pair<Float, Float>>,
    compressFactor: Float = 0.6f
): Float {
    // 累积时间段位移
    var offset = 0f
    var lastEnd = startHour.toFloat()

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


@Composable
fun TimetableSingleSquare(
    items: List<TimeTableItem>,
    modifier: Modifier = Modifier,
    innerPadding : PaddingValues,
    startHour: Int = 8,
    endHour: Int = 24,
    hourHeight: Dp = 70.dp,
    showAll: Boolean = true,
    showLine : Boolean = false,
    zipTime : List<Pair<Float, Float>> = listOf(
        Pair(parseTimeToFloat("12:10"),parseTimeToFloat("14:00"))
    ),
    zipTimeFactor : Float = 0.1f,
    content : @Composable (TimeTableItem) -> Unit
) {
    val hours = endHour - startHour
    // 动画化 showAll 的切换
    val animatedFactor by animateFloatAsState(
        targetValue = if (showAll) 1f else 0f,
    )
    // 平滑列数变化：从5列到7列之间
    val columnCount = (5 + 2 * animatedFactor).coerceIn(5f, 7f)
    // 平滑 padding 变化
    val everyPadding by animateDpAsState(
        targetValue = if (showAll) 1.75.dp else 2.5.dp,
    )

    BoxWithConstraints(
        modifier = modifier
    ) {
        val density = LocalDensity.current
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val hourPx = with(density) { hourHeight.toPx() }
        val columnWidthPx = totalWidthPx / columnCount.toFloat()
        val paddingPx = with(density) { everyPadding.toPx() }
        Column {
            Spacer(Modifier.height(CARD_NORMAL_DP*2))
            InnerPaddingHeight(innerPadding,true)
            Box(
                modifier = Modifier
                    .height(hourHeight * hours)
                    .fillMaxWidth()
                    .let {
                        if(showLine) {
                            it.drawLineTimeTable(columnCount,hourPx,startHour,zipTime,zipTimeFactor,showAll,everyPadding)
                        } else it
                    }
            ) {
                // 按天分组并布局
                val grouped = items.groupBy { it.dayOfWeek }
                grouped.forEach { (day, dayCourses) ->
                    if (!showAll && day !in 1..5) return@forEach

                    val dayIndex = if (showAll) (day - 1).coerceIn(0, 6) else (day - 1).coerceIn(0, 4)

                    val positioned = layoutSquaresForDay(dayCourses)

                    positioned.forEach { p ->
                        // 每列的左右各留 paddingPx 给虚线
                        // 列内并列的 item 之间也应有 gap = paddingPx
                        val xBase = dayIndex * columnWidthPx
                        val innerAvailablePx = columnWidthPx - 2 * paddingPx // 去掉列左右留白
                        val columns = p.totalColumns.coerceAtLeast(1)
                        val gapPx = paddingPx // 并列间隙使用 same everyPadding
                        val totalGapsWidth = gapPx * (columns - 1)
                        val singleWidthPx = (innerAvailablePx - totalGapsWidth) / columns
                        // 防止负宽（极端情况）
                        val safeSingleWidthPx = if (singleWidthPx > 0f) singleWidthPx else (innerAvailablePx / columns)

                        val xOffset = xBase + paddingPx + (safeSingleWidthPx + gapPx) * p.columnIndex
                        val yStart = timeToY(p.start, hourPx, startHour, zipTime,zipTimeFactor)
                        val yEnd = timeToY(p.end, hourPx, startHour,zipTime,zipTimeFactor)
                        val heightPx = yEnd - yStart

                        Box (
                            modifier = Modifier
                                .offset { IntOffset(xOffset.roundToInt(), yStart.roundToInt()) }
                                .width(with(density) { safeSingleWidthPx.toDp() })
                                .height(with(density) { heightPx.toDp() }),
                        ) {
                            content(p.course)
                        }
                    }
                }
            }
            InnerPaddingHeight(innerPadding,false)
        }
    }
}




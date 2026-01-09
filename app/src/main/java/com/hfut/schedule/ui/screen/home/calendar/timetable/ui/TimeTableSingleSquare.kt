package com.hfut.schedule.ui.screen.home.calendar.timetable.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.DEFAULT_END_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.DEFAULT_START_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.MOON_REST_END_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.MOON_REST_START_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.drawLineTimeTable
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.parseTimeToFloat
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.timeToY
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

@Composable
fun TimetableSingleSquare(
    items: List<TimeTableItem>,
    modifier: Modifier = Modifier,
    innerPadding : PaddingValues,
    startTime: Float = parseTimeToFloat(DEFAULT_START_TIME),
    endTime : Float = parseTimeToFloat(DEFAULT_END_TIME),
    hourHeight: Dp = MyApplication.CALENDAR_SQUARE_HEIGHT_NEW.dp,
    showAll: Boolean = true,
    showLine : Boolean = false,
    zipTime : List<Pair<Float, Float>> = listOf(
        Pair(parseTimeToFloat(MOON_REST_START_TIME), parseTimeToFloat(MOON_REST_END_TIME))
    ),
    zipTimeFactor : Float = 0.1f,
    onDoubleTapBlankRegion : ((Offset) -> Unit)? = null,
    onLongTapBlankRegion : ((Offset) -> Unit)? = null,
    onTapBlankRegion : ((Offset) -> Unit)? = null,
    content : @Composable (TimeTableItem) -> Unit
) {
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
        val yEnd = timeToY(endTime, hourPx, startTime,zipTime,zipTimeFactor)
        val columnWidthPx = totalWidthPx / columnCount.toFloat()
        val paddingPx = with(density) { everyPadding.toPx() }
        Column {
            Spacer(Modifier.height(CARD_NORMAL_DP*2))
            InnerPaddingHeight(innerPadding,true)
            Box(
                modifier = Modifier
                    // 从起始时间到24:00
                    .height(with(density) { yEnd.toDp() })
                    .fillMaxWidth()
                    .let {
                        if(showLine) {
                            it.drawLineTimeTable(columnCount)
                        } else it
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = onDoubleTapBlankRegion,
                            onLongPress = onLongTapBlankRegion,
                            onTap = onTapBlankRegion
                        )
                    }
            ) {
                // 按天分组并布局
                val grouped = items.groupBy { it.dayOfWeek }
                grouped.forEach { (day, dayCourses) ->
                    if (!showAll && day !in 1..5) return@forEach

                    val dayIndex = if (showAll) (day - 1).coerceIn(0, 6) else (day - 1).coerceIn(0, 4)

                    val positioned = layoutSquaresForDay(dayCourses)

                    positioned.forEach { item ->
                        // 每列的左右各留 paddingPx 给虚线
                        // 列内并列的 item 之间也应有 gap = paddingPx
                        val xBase = dayIndex * columnWidthPx
                        val innerAvailablePx = columnWidthPx - 2 * paddingPx // 去掉列左右留白
                        val columns = item.totalColumns.coerceAtLeast(1)
                        val gapPx = paddingPx // 并列间隙使用 same everyPadding
                        val totalGapsWidth = gapPx * (columns - 1)
                        val singleWidthPx = (innerAvailablePx - totalGapsWidth) / columns
                        // 防止负宽（极端情况）
                        val safeSingleWidthPx = if (singleWidthPx > 0f) singleWidthPx else (innerAvailablePx / columns)

                        val xOffset = xBase + paddingPx + (safeSingleWidthPx + gapPx) * item.columnIndex
                        // 时间压缩
                        val yStart = timeToY(item.start, hourPx, startTime, zipTime,zipTimeFactor)
                        val yEnd = timeToY(item.end, hourPx, startTime,zipTime,zipTimeFactor)
                        val heightPx = yEnd - yStart

                        Box (
                            modifier = Modifier
                                .offset { IntOffset(xOffset.roundToInt(), yStart.roundToInt()) }
                                .width(with(density) { safeSingleWidthPx.toDp() })
                                .height(with(density) { heightPx.toDp() }),
                        ) {
                            content(item.course)
                        }
                    }
                }
            }
            InnerPaddingHeight(innerPadding,false)
        }
    }
}




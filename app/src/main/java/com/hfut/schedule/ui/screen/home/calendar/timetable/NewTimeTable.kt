package com.hfut.schedule.ui.screen.home.calendar.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.clickableWithRotation
import com.xah.uicommon.style.padding.InnerPaddingHeight
import kotlin.math.roundToInt



private data class PositionedSquareGroup(
    val courses: List<TimeTableItem>,
    val start: Float,
    val end: Float
)

private fun layoutSquaresForDay(items: List<TimeTableItem>): List<PositionedSquareGroup> {
    if (items.isEmpty()) return emptyList()
    val sorted = items.sortedBy { parseTimeToFloat(it.startTime) }

    val merged = mutableListOf<PositionedSquareGroup>()
    var currentGroup = mutableListOf(sorted[0])
    var currentStart = parseTimeToFloat(sorted[0].startTime)
    var currentEnd = parseTimeToFloat(sorted[0].endTime)

    for (i in 1 until sorted.size) {
        val course = sorted[i]
        val start = parseTimeToFloat(course.startTime)
        val end = parseTimeToFloat(course.endTime)

        if (start <= currentEnd) {
            // 与当前分组有重叠，合并
            currentGroup.add(course)
            currentEnd = maxOf(currentEnd, end)
        } else {
            // 不重叠，保存上一个区间
            merged.add(PositionedSquareGroup(currentGroup.toList(), currentStart, currentEnd))
            currentGroup = mutableListOf(course)
            currentStart = start
            currentEnd = end
        }
    }
    // 收尾
    merged.add(PositionedSquareGroup(currentGroup.toList(), currentStart, currentEnd))

    return merged
}

@Composable
fun Timetable(
    items: List<TimeTableItem>,
    modifier: Modifier = Modifier,
    innerPadding : PaddingValues,
    startHour: Int = 8,
    endHour: Int = 24,
    hourHeight: Dp = 70.dp,
    showAll: Boolean = true,
    showLine : Boolean = false,
    zipTime : List<Pair<Float, Float>> = listOf(
        Pair(parseTimeToFloat("12:10"),parseTimeToFloat("14:00")),
    ),
    zipTimeFactor : Float = 0.1f,
    content : @Composable (List<TimeTableItem>) -> Unit
) {
    val hours = endHour - startHour
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    val dividerColor = DividerDefaults.color
    val columnCount = if (showAll) 7 else 5
    val everyPadding = if(showAll) 1.75.dp else 2.5.dp

    BoxWithConstraints(
        modifier = modifier
    ) {
        val density = LocalDensity.current
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val hourPx = with(density) { hourHeight.toPx() }
        val columnWidthPx = totalWidthPx / columnCount.toFloat()
        val paddingPx = with(density) { everyPadding.toPx() }
        Column {
            Spacer(Modifier.height(CARD_NORMAL_DP))
            InnerPaddingHeight(innerPadding,true)
            Box(
                modifier = Modifier
                    .height(hourHeight * hours)
                    .fillMaxWidth()
                    .let {
                        if(showLine) {
                            it.drawBehind {
                                val w = size.width
                                val h = size.height
                                // 虚线在列边界
                                for (i in 1..columnCount-1) {
                                    val x = w * i / columnCount.toFloat()
                                    drawLine(
                                        color = dividerColor,
                                        strokeWidth = 1.dp.toPx(),
                                        start = Offset(x, 0f),
                                        end = Offset(x, h),
                                        pathEffect = dashEffect
                                    )
                                }
                            }
                        } else
                            it
                    }
            ) {
                // 按天分组并布局
                val grouped = items.groupBy { it.dayOfWeek }

                grouped.forEach { (day, dayCourses) ->
                    if (!showAll && day !in 1..5) return@forEach

                    val dayIndex = if (showAll) (day - 1).coerceIn(0, 6) else (day - 1).coerceIn(0, 4)

                    val positionedGroups = layoutSquaresForDay(dayCourses)

                    positionedGroups.forEach { group ->
                        val xBase = dayIndex * columnWidthPx
                        val xOffset = xBase + paddingPx
                        val innerAvailablePx = columnWidthPx - 2 * paddingPx

                        val yStart = timeToY(group.start, hourPx, startHour, zipTime,zipTimeFactor)
                        val yEnd = timeToY(group.end, hourPx, startHour,zipTime,zipTimeFactor)
                        val heightPx = yEnd - yStart
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(xOffset.roundToInt(), yStart.roundToInt()) }
                                .width(with(density) { innerAvailablePx.toDp() })
                                .height(with(density) { heightPx.toDp() })
                        ) {
                            content(group.courses)
                        }
                    }
                }
            }
            InnerPaddingHeight(innerPadding,false)
        }
    }
}
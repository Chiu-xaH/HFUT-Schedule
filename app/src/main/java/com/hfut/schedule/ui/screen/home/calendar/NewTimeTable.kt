package com.hfut.schedule.ui.screen.home.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.clickableWithRotation
import kotlin.math.roundToInt

private data class PositionedCourse(
    val course: TimeTableItem,
    val start: Float,
    val end: Float,
    val columnIndex: Int,
    val totalColumns: Int
)

private fun layoutCoursesForDay(dayCourses: List<TimeTableItem>): List<PositionedCourse> {
    if (dayCourses.isEmpty()) return emptyList()
    val sorted = dayCourses.sortedBy { parseTimeToFloat(it.startTime) }

    val positioned = mutableListOf<PositionedCourse>()
    val active = mutableListOf<PositionedCourse>()

    for (course in sorted) {
        val start = parseTimeToFloat(course.startTime)
        val end = parseTimeToFloat(course.endTime)

        // 移除已结束的课程
        active.removeAll { it.end <= start }

        // 当前活跃的课程数量即冲突列数
        val columnIndex = active.size
        val totalColumns = active.size + 1

        val pc = PositionedCourse(course, start, end, columnIndex, totalColumns)
        active.add(pc)
        positioned.add(pc)
    }

    // 计算每个课程真实的 totalColumns（即组最大列数）
    return positioned.map { p ->
        val overlapping = positioned.filter {
            it.course.dayOfWeek == p.course.dayOfWeek &&
                    it.start < p.end && it.end > p.start
        }
        p.copy(totalColumns = overlapping.maxOf { it.columnIndex + 1 })
    }
}

interface TimeTableDataAdapter {
    // 适配器
    fun<T> toTimeTableData(originalData : T) : List<TimeTableItem>
}

enum class TimeTableType(
    val description: String
) {
    COURSE("课程"),FOCUS("日程"),EXAM("考试")
}

/**
 * @param startTime 传入HH-MM
 * @param endTime 传入HH-MM
 * @param dayOfWeek 周几 注意周日是7
 */
data class TimeTableItem(
    val type : TimeTableType,
    val name: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val place : String? = null,
)

@Composable
fun Timetable(
    courses: List<TimeTableItem>,
    modifier: Modifier = Modifier,
    startHour: Int = 5,
    endHour: Int = 22,
    hourHeight: Dp = 75.dp,
    showAll: Boolean = true,
    onCourseClickable : (TimeTableItem) -> Unit,
    content : @Composable (TimeTableItem) -> Unit
) {
    val hours = endHour - startHour
    val scrollState = rememberScrollState()
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    val dividerColor = DividerDefaults.color
    val columnCount = if (showAll) 7 else 5
    val everyPadding = if(showAll) 1.75.dp else 2.5.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        val density = LocalDensity.current
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val hourPx = with(density) { hourHeight.toPx() }
        val columnWidthPx = totalWidthPx / columnCount.toFloat()
        val paddingPx = with(density) { everyPadding.toPx() }

        Box(
            modifier = Modifier
                .height(hourHeight * hours)
                .fillMaxWidth()
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    // 虚线在列边界
                    for (i in 0..columnCount) {
                        val x = w * i / columnCount.toFloat()
                        drawLine(
                            color = dividerColor,
                            strokeWidth = 1.dp.toPx(),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            pathEffect = dashEffect
                        )
                    }

                    // 横线（每小时一条），如果需要也可以加 dashEffect
                    for (i in 0..hours step 2) {
                        val y = i * hourPx
                        drawLine(
                            color = dividerColor.copy(alpha = 0.6f),
                            strokeWidth = 1.dp.toPx(),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            pathEffect = dashEffect
                        )
                    }
                }
        ) {
            // 按天分组并布局
            val grouped = courses.groupBy { it.dayOfWeek }

            grouped.forEach { (day, dayCourses) ->
                if (!showAll && day !in 1..5) return@forEach

                val dayIndex = if (showAll) (day - 1).coerceIn(0, 6) else (day - 1).coerceIn(0, 4)

                val positioned = layoutCoursesForDay(dayCourses)

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
                    val yStart = (p.start - startHour) * hourPx
                    val heightPx = (p.end - p.start) * hourPx

                    Surface (
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .offset { IntOffset(xOffset.roundToInt(), yStart.roundToInt()) }
                            .width(with(density) { safeSingleWidthPx.toDp() })
                            .height(with(density) { heightPx.toDp() })
                            .clickableWithRotation(ClickScale.SMALL.scale) {
                                onCourseClickable(p.course)
                            },
                    ) {
                        content(p.course)
                    }
                }
            }
        }
    }
}


private fun parseTimeToFloat(time: String): Float {
    val parts = time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return hour + minute / 60f
}


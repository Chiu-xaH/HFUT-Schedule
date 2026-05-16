package com.hfut.schedule.ui.screen.home.calendar.timetable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.text.AutoSizeText
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.MOON_REST_END_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.MOON_REST_START_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.parseTimeToFloat
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.timeToY
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.ColumnVertical
import com.sharednav.common.util.NoneRoundShape
import com.xah.floating.util.LocalFloatingController
import kotlin.math.roundToInt

private val padding = 4.dp
private const val columnCount = 4
private const val endCourseTime = "21:50"
private val itemWidth = 90.dp
private val itemHeight = 150.dp

@Composable
fun TimeTablePreview(
    items: List<List<TimeTableItem>>,
    currentWeek : Int,
    title : String = "第${currentWeek}周",
    onItemClick : (Int) -> Unit,
) {
    val controller = LocalFloatingController.current

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = NoneRoundShape,
    ) {
        Box {
            var innerPadding by remember { mutableStateOf(0.dp) }

            LazyVerticalGrid(
//            columns = GridCells.Adaptive(minSize = itemWidth),
                columns = GridCells.Fixed(columnCount),
                modifier = Modifier.padding(horizontal = padding),
                horizontalArrangement = Arrangement.spacedBy(padding),
                verticalArrangement = Arrangement.spacedBy(padding)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(Modifier.height(innerPadding+APP_HORIZONTAL_DP-padding))
                }
                items(items.size) { week ->
                    val list = items[week]
                    val isCurrentWeek = currentWeek == week+1

                    ColumnVertical {
                        MiniTimetablePreview(
                            items = list,
                            endTime = parseTimeToFloat(endCourseTime),
                            modifier = Modifier
                                .height(itemHeight)
                                .width(itemWidth)
                                .border(
                                    1.dp,
                                    if (isCurrentWeek) MaterialTheme.colorScheme.primary else DividerDefaults.color,
                                    MaterialTheme.shapes.small
                                )
                                .padding(padding)
                                .clickable {
                                    onItemClick(week+1)
                                }
                        )
                        Text("第${week+1}周" ,modifier = Modifier.padding(top = padding),
                            style = MaterialTheme.typography.labelSmall.copy(if(isCurrentWeek) MaterialTheme.colorScheme.primary else Color.Gray))
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(Modifier.height(APP_HORIZONTAL_DP+padding))
                }
            }

            AutoSizeText(
                title,
                innerPadding,
                Modifier
                    .align(Alignment.TopStart)
                    .padding(vertical = APP_HORIZONTAL_DP/2, horizontal = APP_HORIZONTAL_DP/2)
            )
            LiquidButton(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(APP_HORIZONTAL_DP/2)
                        .measureDpSize { _,h -> innerPadding = h }
                ,
                onClick = {
                    controller.pop()
                },
                backdrop = rememberLayerBackdrop(),
                isCircle = true
            ) {
                Icon(painterResource(R.drawable.close),null)
            }
        }
    }
}

@Composable
private fun MiniTimetablePreview(
    items: List<TimeTableItem>,
    modifier: Modifier = Modifier,
    startTime: Float = 8f,
    endTime: Float = parseTimeToFloat(endCourseTime),
    zipTime: List<Pair<Float, Float>> = listOf(
        Pair(parseTimeToFloat(MOON_REST_START_TIME), parseTimeToFloat(MOON_REST_END_TIME)),
    ),
    zipTimeFactor: Float = 0.1f
) {
    val density = LocalDensity.current
    val columnCount = if(items.find { it.dayOfWeek in 6..7 } == null) 5 else 7
    val colors = mapOf(
        TimeTableType.COURSE to MaterialTheme.colorScheme.primaryContainer,
        TimeTableType.FOCUS to MaterialTheme.colorScheme.primary,
        TimeTableType.EXAM to MaterialTheme.colorScheme.errorContainer,
    )

    BoxWithConstraints(
        modifier = modifier
    ) {
        val totalWidthPx = with(density) { maxWidth.toPx() }
        val totalHeightPx = with(density) { maxHeight.toPx() }
        val compressedDuration = (endTime - startTime) - zipTime.sumOf { (s, e) ->
            ((e - s) * (1 - zipTimeFactor)).toDouble()
        }.toFloat()

        val hourPx = totalHeightPx / compressedDuration
        val columnWidthPx = totalWidthPx / columnCount
        val yEnd = timeToY(endTime, hourPx, startTime, zipTime, zipTimeFactor)
        val totalHeightDp = with(density) { yEnd.toDp() }

        Box(
            Modifier
                .fillMaxWidth()
                .height(totalHeightDp)
        ) {
            // 绘制课程色块
            items.groupBy { it.dayOfWeek }.forEach { (day, dayCourses) ->
                val dayIndex = (day - 1).coerceIn(0, columnCount - 1)

                dayCourses.forEach { course ->
                    val start = parseTimeToFloat(course.startTime)
                    val end = parseTimeToFloat(course.endTime)

//                    val yStart = timeToY(start, hourPx, startTime, zipTime, zipTimeFactor)
//                    val yEndEach = timeToY(end, hourPx, startTime, zipTime, zipTimeFactor)
//                    val heightPx = (yEndEach - yStart).coerceAtLeast(2f)
                    val xOffset = dayIndex * columnWidthPx

                    val actualStart = start.coerceAtLeast(startTime)
                    val actualEnd = end.coerceAtMost(endTime)

                    if (actualEnd <= startTime || actualStart >= endTime) return@forEach  // 完全不在范围内的直接跳过

                    val yStart = timeToY(actualStart, hourPx, startTime, zipTime, zipTimeFactor)
                    val yEndEach = timeToY(actualEnd, hourPx, startTime, zipTime, zipTimeFactor)
                    val heightPx = (yEndEach - yStart).coerceAtLeast(2f)

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    xOffset.roundToInt(),
                                    yStart.roundToInt()
                                )
                            }
                            .width(with(density) { columnWidthPx.toDp() })
                            .height(with(density) { heightPx.toDp() })
                            .padding(horizontal = 1.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(
                                colors[course.type] ?: MaterialTheme.colorScheme.primaryContainer
                            )
                    )
                }
            }
        }
    }
}
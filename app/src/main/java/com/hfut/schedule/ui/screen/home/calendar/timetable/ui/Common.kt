package com.hfut.schedule.ui.screen.home.calendar.timetable.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.calendarSquareGlass
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.DEFAULT_START_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.parseTimeToFloat
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.mirror.util.ShaderState
import com.xah.transition.component.containerShare
import com.xah.transition.state.LocalAppNavController
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.clickableWithScale

@Composable
fun TimeTable(
    items: List<List<TimeTableItem>>,
    week : Int,
    showAll: Boolean,
    innerPadding : PaddingValues,
    modifier: Modifier = Modifier,
    squareModifier : Modifier = Modifier,
    shaderState : ShaderState? = null,
    onTapBlankRegion : ((Offset) -> Unit)? = null,
    onLongTapBlankRegion : ((Offset) -> Unit)? = null,
    onDoubleTapBlankRegion : ((Offset) -> Unit)? = null,
    onSquareClick : (List<TimeTableItem>) -> Unit,
) {
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val calendarSquareHeight by DataStoreManager.calendarSquareHeightNew.collectAsState(initial = MyApplication.CALENDAR_SQUARE_HEIGHT_NEW)
    val enableMergeSquare by DataStoreManager.enableMergeSquare.collectAsState(initial = false)
    val calendarSquareTextSize by DataStoreManager.calendarSquareTextSize.collectAsState(initial = 1f)

    val list = if(week >= items.size || week > MyApplication.MAX_WEEK) {
        Exception("NewTimeTableUI received week out of bounds for length ${items.size} of items[${week-1}]").printStackTrace()
        emptyList()
    }  else {
        items[week-1]
    }
    val lineHeight = (if(!showAll) 19.sp else 16.sp) * calendarSquareTextSize
    val textSize = (if(!showAll) 13.sp else 11.sp) * calendarSquareTextSize
    val timeTextSize = (textSize.value-1).sp
    val hasBackground = shaderState != null

    val earliestTime = list.minOfOrNull { it.startTime }
    val startTime = earliestTime?.let {
        minOf(parseTimeToFloat(it), DEFAULT_START_TIME)
    } ?: DEFAULT_START_TIME

    if(enableMergeSquare) {
        TimetableCommonSquare(
            items = list,
            modifier = modifier,
            showAll = showAll,
            showLine = !hasBackground,
            innerPadding = innerPadding,
            hourHeight = calendarSquareHeight.dp,
            startTime = startTime,
            onDoubleTapBlankRegion = onDoubleTapBlankRegion,
            onLongTapBlankRegion = onLongTapBlankRegion,
            onTapBlankRegion = onTapBlankRegion
        ) { list ->
            val color: Pair<Color, Color> = if (!hasBackground) {
                when {
                    list.size > 1 -> Pair(
                        MaterialTheme.colorScheme.errorContainer,
                        MaterialTheme.colorScheme.onErrorContainer.copy(.6f)
                    )

                    else -> Pair(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(.6f)
                    )
                }
            } else {
                Pair(
                    MaterialTheme.colorScheme.surfaceContainer,
                    MaterialTheme.colorScheme.onSurface.copy(.6f)
                )
            }
            Surface(
                color = if (!hasBackground) color.first else Color.Transparent,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = squareModifier
                    .let {
                        if (hasBackground) {
                            it
                                .clip(MaterialTheme.shapes.extraSmall)
                                .let {
                                    if (AppVersion.CAN_SHADER) {
                                        it.calendarSquareGlass(
                                            shaderState,
                                            MaterialTheme.colorScheme.surface.copy(
                                                customBackgroundAlpha
                                            ),
                                            enableLiquidGlass,
                                        )
                                    } else {
                                        it
                                    }
                                }
                        } else {
                            it
                        }
                    }
                    .let {
                        if (!hasBackground) {
                            it.clickableWithScale(ClickScale.SMALL.scale) {
                                onSquareClick(list)
                            }
                        } else {
                            it.clickable {
                                onSquareClick(list)
                            }
                        }
                    }
                    .let {
                        if (!hasBackground && list.size == 1) {
                            val item = list[0]
                            val origin = CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"
                            when (item.type) {
                                TimeTableType.COURSE -> {
                                    it.containerShare(
                                        AppNavRoute.CourseDetail.withArgs(item.name, origin), MaterialTheme.shapes.extraSmall
                                    )
                                }
                                TimeTableType.FOCUS -> {
                                    item.id?.let { id ->
                                        it.containerShare(AppNavRoute.AddEvent.withArgs(id, origin), MaterialTheme.shapes.extraSmall)
                                    } ?: it
                                }
                                TimeTableType.EXAM -> {
                                    it.containerShare(AppNavRoute.Exam.withArgs(origin), MaterialTheme.shapes.extraSmall)
                                }
                            }
                        } else {
                            it
                        }
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (list.size == 1) {
                        val item = list[0]

                        Text(
                            text = item.startTime,
                            fontSize = timeTextSize,
                            textAlign = TextAlign.Center,
                            lineHeight = lineHeight,
                            overflow = TextOverflow.Clip,
                            maxLines = 1,
                            color = color.second,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f) // 占据中间剩余的全部空间
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.name + (item.teacher?.let { "@$it" } ?: ""),
                                fontSize = textSize,
                                textAlign = TextAlign.Center,
                                lineHeight = lineHeight,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item.place?.let {
                            Text(
                                text = it,
                                fontSize = timeTextSize,
                                textAlign = TextAlign.Center,
                                lineHeight = lineHeight,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Text(
                            text = item.endTime,
                            fontSize = timeTextSize,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Clip,
                            maxLines = 1,
                            lineHeight = lineHeight,
                            color = color.second,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else if (list.size > 1) {
                        val startTime = list.minOf { it.startTime }
                        val endTime = list.maxOf { it.endTime }
                        val courses = list.joinToString(",") { it.name.substring(0, 1) }

                        Text(
                            text = startTime,
                            fontSize = timeTextSize,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Clip,
                            maxLines = 1,
                            lineHeight = lineHeight,
                            color = color.second,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f) // 占据中间剩余的全部空间
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "冲突${list.size}项",
                                fontSize = textSize,
                                lineHeight = lineHeight,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Text(
                            text = courses,
                            fontSize = timeTextSize,
                            textAlign = TextAlign.Center,
                            lineHeight = lineHeight,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = endTime,
                            fontSize = timeTextSize,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Clip,
                            maxLines = 1,
                            lineHeight = lineHeight,
                            color = color.second,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    } else {
        TimetableSingleSquare(
            items = list,
            modifier = modifier,
            showAll = showAll,
            showLine = !hasBackground,
            innerPadding = innerPadding,
            hourHeight = calendarSquareHeight.dp,
            startTime = startTime,
            onDoubleTapBlankRegion = onDoubleTapBlankRegion,
            onLongTapBlankRegion = onLongTapBlankRegion,
            onTapBlankRegion = onTapBlankRegion
        ) { item ->
            val color: Pair<Color, Color> = if (!hasBackground) {
                when (item.type) {
                    TimeTableType.FOCUS -> Pair(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.onPrimary.copy(.6f)
                    )

                    TimeTableType.COURSE -> Pair(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(.6f)
                    )

                    TimeTableType.EXAM -> Pair(
                        MaterialTheme.colorScheme.errorContainer,
                        MaterialTheme.colorScheme.onErrorContainer.copy(.6f)
                    )
                }
            } else {
                Pair(
                    MaterialTheme.colorScheme.surfaceContainer,
                    MaterialTheme.colorScheme.onSurface.copy(.6f)
                )
            }
            Surface(
                color = if (!hasBackground) color.first else Color.Transparent,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = squareModifier
                    .let {
                        if (hasBackground) {
                            it
                                .clip(MaterialTheme.shapes.extraSmall)
                                .let {
                                    if (AppVersion.CAN_SHADER) {
                                        it.calendarSquareGlass(
                                            shaderState,
                                            MaterialTheme.colorScheme.surface.copy(
                                                customBackgroundAlpha
                                            ),
                                            enableLiquidGlass,
                                        )
                                    } else {
                                        it
                                    }
                                }
                        } else {
                            it
                        }
                    }
                    .let {
                        if (!hasBackground) {
                            it.clickableWithScale(ClickScale.SMALL.scale) {
                                onSquareClick(listOf(item))
                            }
                        } else {
                            it.clickable {
                                onSquareClick(listOf(item))
                            }
                        }
                    }
                    .let {
                        if (!hasBackground) {
                            val origin = CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"
                            when (item.type) {
                                TimeTableType.COURSE -> {
                                    it.containerShare(
                                        AppNavRoute.CourseDetail.withArgs(item.name, origin), MaterialTheme.shapes.extraSmall
                                    )
                                }
                                TimeTableType.FOCUS -> {
                                    item.id?.let { id ->
                                        it.containerShare(AppNavRoute.AddEvent.withArgs(id, origin), MaterialTheme.shapes.extraSmall)
                                    } ?: it
                                }
                                TimeTableType.EXAM -> {
                                    it.containerShare(AppNavRoute.Exam.withArgs(origin), MaterialTheme.shapes.extraSmall)
                                }
                            }
                        } else {
                            it
                        }
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = item.startTime,
                        fontSize = timeTextSize,
                        lineHeight = lineHeight,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Clip,
                        maxLines = 1,
                        color = color.second,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f) // 占据中间剩余的全部空间
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.name + (item.teacher?.let { "@$it" } ?: ""),
                            lineHeight = lineHeight,
                            fontSize = textSize,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item.place?.let {
                            Text(
                                text = it,
                                fontSize = timeTextSize,
                                lineHeight = lineHeight,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Text(
                            text = item.endTime,
                            fontSize = timeTextSize,
                            textAlign = TextAlign.Center,
                            lineHeight = lineHeight,
                            overflow = TextOverflow.Clip,
                            maxLines = 1,
                            color = color.second,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


package com.hfut.schedule.ui.screen.home.calendar.timetable.ui

import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.nav.destination.AddEventDestination
import com.hfut.schedule.ui.nav.destination.CourseDetailApiDestination
import com.hfut.schedule.ui.nav.destination.ExamDestination
import com.hfut.schedule.ui.nav.window.TimeTableSquareWindow
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.DEFAULT_END_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.DEFAULT_START_TIME
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.parseTimeToFloat
import com.hfut.schedule.ui.style.special.calendarSquareGlass
import com.sharednav.common.util.NoneRoundShape
import com.xah.common.ui.shader.ShaderState
import com.xah.container.component.base.sharedContainer
import com.xah.container.model.ContainerFilledStrategy
import com.xah.floating.util.LocalFloatingController

import com.xah.common.logic.util.LogUtil

const val timeTextFactor = 0.85
const val placeTextFactor = 0.9

@Composable
fun TimeTable(
    items: List<List<TimeTableItem>>,
    week : Int,
    showAll: Boolean,
    innerPadding : PaddingValues,
    modifier: Modifier = Modifier,
    squareModifier : Modifier = Modifier,
    scaleFactor : Float = 1f,
    shaderState : ShaderState? = null,
    onTapBlankRegion : ((Offset) -> Unit)? = null,
    onLongTapBlankRegion : ((Offset) -> Unit)? = null,
    onDoubleTapBlankRegion : ((Offset) -> Unit)? = null,
    onSquareClick : ((List<TimeTableItem>) -> Unit)?,
) {
    val floatingController = LocalFloatingController.current

    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = MyApplication.CALENDAR_SQUARE_ALPHA)
    val calendarSquareHeight by DataStoreManager.calendarSquareHeight.collectAsState(initial = MyApplication.CALENDAR_SQUARE_HEIGHT)
    val calendarSquareTextSize by DataStoreManager.calendarSquareTextSize.collectAsState(initial = 1f)
    val calendarSquareTextPadding by DataStoreManager.calendarSquareTextPadding.collectAsState(initial = MyApplication.CALENDAR_SQUARE_TEXT_PADDING)

    val enableMergeSquare by DataStoreManager.enableMergeSquare.collectAsState(initial = false)

    val list = remember(items,week) {
        if(week > items.size || week > MyApplication.MAX_WEEK) {
            LogUtil.error("NewTimeTableUI received week out of bounds for length ${items.size} of items[${week-1}]")
            emptyList()
        }  else {
            items[week-1]
        }
    }

    val textSize = remember(showAll,calendarSquareTextSize) { (if (!showAll) 12.5.sp else 11.sp) * calendarSquareTextSize }
    val lineHeight = remember(textSize,calendarSquareTextPadding) { textSize * calendarSquareTextPadding }
    val timeTextLineHeight = remember(lineHeight) { lineHeight * timeTextFactor }
    val timeTextSize = remember(textSize) { textSize * timeTextFactor }
    val placeTextLineHeight = remember(lineHeight) { lineHeight * placeTextFactor }
    val placeTextSize = remember(textSize) { textSize * placeTextFactor }

    val hasBackground = remember(shaderState) { shaderState != null }
    val noAlpha = remember(customBackgroundAlpha) { customBackgroundAlpha == 1f }

    val startTime = remember(list) {
        val earliestTime = list.minOfOrNull { it.startTime }
        parseTimeToFloat(DEFAULT_START_TIME).let { defaultTime ->
            earliestTime?.let {
                minOf(parseTimeToFloat(it), defaultTime)
            } ?: defaultTime
        }
    }
    val endTime = remember(list) {
        parseTimeToFloat(DEFAULT_END_TIME).let { defaultTime ->
            val latestTime = list.maxOfOrNull { it.endTime }
            latestTime?.let {
                maxOf(parseTimeToFloat(it), defaultTime)
            } ?: defaultTime
        }
    }


    key(noAlpha,hasBackground) {
        if(enableMergeSquare) {
            TimetableCommonSquare(
                items = list,
                modifier = modifier,
                showAll = showAll,
                showLine = !hasBackground,
                innerPadding = innerPadding,
                hourHeight = calendarSquareHeight.dp*scaleFactor,
                startTime = startTime,
                endTime = endTime,
                onDoubleTapBlankRegion = onDoubleTapBlankRegion,
                onLongTapBlankRegion = onLongTapBlankRegion,
                onTapBlankRegion = onTapBlankRegion
            ) { list ->
                val color: Pair<Color, Color> = if (!hasBackground) {
                    if(list.size == 1) {
                        when (list[0].type) {
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
                    } else if(list.size > 1) {
                        Pair(
                            MaterialTheme.colorScheme.error,
                            MaterialTheme.colorScheme.onError.copy(.6f)
                        )
                    } else {
                        Pair(
                            MaterialTheme.colorScheme.surfaceContainer,
                            MaterialTheme.colorScheme.onSurface.copy(.6f)
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
                    shape = NoneRoundShape,
                    modifier = squareModifier
                        .let {
                            if (list.size == 1) {
                                val item = list[0]
                                val origin =
                                    CourseDetailOrigin.CALENDAR_JXGLSTU.t + "${item.hashCode()}"
                                val key = when (item.type) {
                                    TimeTableType.COURSE -> {
                                        CourseDetailApiDestination(item.name, origin, item.place)
                                    }

                                    TimeTableType.FOCUS -> {
                                        AddEventDestination(
                                            item.detail.eventId,
                                            CourseDetailOrigin.CALENDAR_JXGLSTU.t
                                        )
                                    }

                                    TimeTableType.EXAM -> {
                                        ExamDestination(origin)
                                    }
                                }.key

                                it.sharedContainer(
                                    key,
                                    MaterialTheme.shapes.extraSmall,
                                    containerFilledStrategy =
                                        if (hasBackground) {
                                            if (noAlpha) {
                                                ContainerFilledStrategy.Pixel(
                                                    ContainerFilledStrategy.Color(MaterialTheme.colorScheme.surface)
                                                )
                                            } else {
                                                ContainerFilledStrategy.Clip
                                            }
                                        } else {
                                            ContainerFilledStrategy.Pixel(
                                                ContainerFilledStrategy.Color(color.first)
                                            )
                                        }
                                )

                            } else {
                                it.sharedContainer(
                                    null,
                                    MaterialTheme.shapes.extraSmall,
                                    containerFilledStrategy =
                                        if (hasBackground) {
                                            if (noAlpha) {
                                                ContainerFilledStrategy.Pixel(
                                                    ContainerFilledStrategy.Color(MaterialTheme.colorScheme.surface)
                                                )
                                            } else {
                                                ContainerFilledStrategy.Clip
                                            }
                                        } else {
                                            ContainerFilledStrategy.Pixel(
                                                ContainerFilledStrategy.Color(color.first)
                                            )
                                        }
                                )
                            }
                        }
                        .let {
                            if (hasBackground) {
                                it.calendarSquareGlass(
                                    shaderState!!,
                                    MaterialTheme.colorScheme.surface.copy(customBackgroundAlpha)
                                )
                            } else {
                                it
                            }
                        }
                        .combinedClickable(
                            onLongClick = {
                                floatingController.push(TimeTableSquareWindow(list))
                            },
                            onClick = {
                                onSquareClick?.let { it(list) } ?: floatingController.push(
                                    TimeTableSquareWindow(list)
                                )
                            }
                        )
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
                                lineHeight = timeTextLineHeight,
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
                            item.getSimplyPlace()?.let {
                                Text(
                                    text = it,
                                    fontSize = placeTextSize,
                                    textAlign = TextAlign.Center,
                                    lineHeight = placeTextLineHeight,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Text(
                                text = item.endTime,
                                fontSize = timeTextSize,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Clip,
                                maxLines = 1,
                                lineHeight = timeTextLineHeight,
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
                                lineHeight = timeTextLineHeight,
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
                                fontSize = placeTextSize,
                                textAlign = TextAlign.Center,
                                lineHeight = placeTextLineHeight,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = endTime,
                                fontSize = timeTextSize,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Clip,
                                maxLines = 1,
                                lineHeight = timeTextLineHeight,
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
                hourHeight = calendarSquareHeight.dp*scaleFactor,
                startTime = startTime,
                endTime = endTime,
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
                    shape = NoneRoundShape,
                    modifier = squareModifier
                        .let {
                            val origin =
                                CourseDetailOrigin.CALENDAR_JXGLSTU.t + "${item.hashCode()}"
                            val key = when (item.type) {
                                TimeTableType.COURSE -> {
                                    CourseDetailApiDestination(item.name, origin, item.place)
                                }

                                TimeTableType.FOCUS -> {
                                    AddEventDestination(
                                        item.detail.eventId,
                                        CourseDetailOrigin.CALENDAR_JXGLSTU.t
                                    )
                                }

                                TimeTableType.EXAM -> {
                                    ExamDestination(origin)
                                }
                            }.key

                            it.sharedContainer(
                                key,
                                MaterialTheme.shapes.extraSmall,
                                containerFilledStrategy =
                                    if (hasBackground) {
                                        if (noAlpha) {
                                            ContainerFilledStrategy.Pixel(
                                                ContainerFilledStrategy.Color(MaterialTheme.colorScheme.surface)
                                            )
                                        } else {
                                            ContainerFilledStrategy.Clip
                                        }
                                    } else {
                                        ContainerFilledStrategy.Pixel(
                                            ContainerFilledStrategy.Color(color.first)
                                        )
                                    }
                            )
                        }
                        .let {
                            if (hasBackground) {
                                it.calendarSquareGlass(
                                    shaderState!!,
                                    MaterialTheme.colorScheme.surface.copy(customBackgroundAlpha)
                                )
                            } else {
                                it
                            }
                        }
                        .combinedClickable(
                            onLongClick = {
                                floatingController.push(TimeTableSquareWindow(listOf(item)))
                            },
                            onClick = {
                                onSquareClick?.let { it(listOf(item)) } ?: floatingController.push(
                                    TimeTableSquareWindow(listOf(item))
                                )
                            }
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = item.startTime,
                            fontSize = timeTextSize,
                            lineHeight = timeTextLineHeight,
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
                            item.getSimplyPlace()?.let {
                                Text(
                                    text = it,
                                    fontSize = placeTextSize,
                                    lineHeight = placeTextLineHeight,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Text(
                                text = item.endTime,
                                fontSize = timeTextSize,
                                textAlign = TextAlign.Center,
                                lineHeight = timeTextLineHeight,
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
}


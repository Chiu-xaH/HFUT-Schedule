package com.hfut.schedule.ui.screen.home.calendar.timetable

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper.entityToDto
import com.hfut.schedule.logic.model.jxglstu.DatumResponse
import com.hfut.schedule.logic.network.util.toStr
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.status.DevelopingUI
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.calendarSquareGlass
import com.hfut.schedule.ui.screen.home.calendar.common.dateToWeek
import com.hfut.schedule.ui.screen.home.calendar.common.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import com.hfut.schedule.ui.style.CalendarStyle
import com.xah.mirror.util.ShaderState
import com.xah.transition.component.containerShare
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.clickableWithRotation
import com.xah.uicommon.style.clickableWithScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import kotlin.String

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
    val id : Int? = null
)

enum class TimeTableType(
    val description: String,
    val icon : Int
) {
    COURSE("课程",R.drawable.calendar),FOCUS("日程",R.drawable.lightbulb),EXAM("考试",R.drawable.draw)
}


fun parseTimeToFloat(time: String): Float {
    val parts = time.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return hour + minute / 60f
}

// 分配20周
suspend fun allToTimeTableData(context: Context): List<List<TimeTableItem>> = withContext(Dispatchers.Default) {
    // 并发调用三个数据源
    val jxglstuDeferred = async { jxglstuToTimeTableData(context) }
    val focusDeferred = async { focusToTimeTableData() }
    val examDeferred = async { examToTimeTableData(context) }

    val jxglstuList = jxglstuDeferred.await()
    val focusList = focusDeferred.await()
    val examList = examDeferred.await()

    // 合并：按周（index）叠加三个来源
    List(MyApplication.MAX_WEEK) { i ->
        jxglstuList[i] + focusList[i]+ examList[i]
    }
}

suspend fun jxglstuToTimeTableData(context: Context): List<List<TimeTableItem>> {
    val json = LargeStringDataManager.read(context, LargeStringDataManager.DATUM)
        ?: return List(20) { emptyList<TimeTableItem>() }
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }

        val datumResponse = Gson().fromJson(json, DatumResponse::class.java)
        val scheduleList = datumResponse.result.scheduleList
        val lessonList = datumResponse.result.lessonList
        // 根据id得到课程名
        val hashMap = mutableMapOf<String, String>()
        for(item in lessonList) {
            hashMap[item.id] = item.courseName
        }

        for(item in scheduleList) {
            val list = result[item.weekIndex-1]
            list.add(TimeTableItem(
                type = TimeTableType.COURSE,
                name = item.lessonId.toString().let { hashMap[it] ?: it },
                dayOfWeek = item.weekday,
                startTime = parseTime(item.startTime),
                endTime = parseTime(item.endTime),
                place  = item.room?.nameZh?.replace("学堂",""),
            ))
        }
        // 去重
        distinctUnit(result)
        return result
    } catch (e : Exception) {
        e.printStackTrace()
        return List(20) { emptyList<TimeTableItem>() }
    }
}

suspend fun focusToTimeTableData(): List<List<TimeTableItem>> {
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }
        val focusList = DataBaseManager.customEventDao.getAll(CustomEventType.SCHEDULE.name).map {
            entityToDto(it)
        }
        for(item in focusList) {
            val start = item.dateTime.start.toStr().split(" ")
            if(start.size != 2) {
                continue
            }
            val startDate = start[0]
            val startTime = start[1]
            val weekInfo = dateToWeek(startDate) ?: continue

            val end = item.dateTime.end.toStr().split(" ")
            if(end.size != 2) {
                continue
            }
            val endDate = end[0]
            val endTime = end[1]

            // 是同一周
            val list = result[weekInfo.first-1]
            val name = item.title
            val place = item.description?.replace("学堂","")

            // 跨天日程将其分裂
            if(endDate != startDate) {
                var currentDate = LocalDate.parse(startDate)
                val endLocalDate = LocalDate.parse(endDate)

                while (!currentDate.isAfter(endLocalDate)) {
                    val isFirstDay = currentDate == LocalDate.parse(startDate)
                    val isLastDay = currentDate == endLocalDate

                    val currentWeek = dateToWeek(currentDate.toString()) ?: continue
                    val list = result[currentWeek.first - 1]

                    val currentStartTime = when {
                        isFirstDay -> startTime
                        else -> "00:00"
                    }
                    val currentEndTime = when {
                        isLastDay -> endTime
                        else -> "24:00"
                    }

                    list.add(
                        TimeTableItem(
                            type = TimeTableType.FOCUS,
                            name = name,
                            dayOfWeek = currentWeek.second,
                            startTime = currentStartTime,
                            endTime = currentEndTime,
                            place = place,
                            id = item.id
                        )
                    )
                    currentDate = currentDate.plusDays(1)
                }
            } else {
                // 同一天
                list.add(TimeTableItem(
                    type = TimeTableType.FOCUS,
                    name = name,
                    dayOfWeek = weekInfo.second,
                    startTime = startTime,
                    endTime = endTime,
                    place  = place,
                    id = item.id
                ))
            }
        }
        return result
    } catch (e : Exception) {
        e.printStackTrace()
        return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    }
}

suspend fun examToTimeTableData(context : Context): List<List<TimeTableItem>> {
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }
        val examList = examToCalendar(context)
        for (item in examList) {
            val startTime = item.startTime ?: continue
            val endTime = item.endTime ?: continue
            val startDate = item.day ?: continue
            val weekInfo = dateToWeek(startDate) ?: continue

            val list = result[weekInfo.first-1]

            val name = item.course ?: continue
            val place = item.place
            list.add(TimeTableItem(
                type = TimeTableType.EXAM,
                name = name,
                dayOfWeek = weekInfo.second,
                startTime = startTime,
                endTime = endTime,
                place  = place,
            ))
        }
        return result
    } catch (e : Exception) {
        e.printStackTrace()
        return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    }
}

suspend fun communityToTimeTableData() : List<List<TimeTableItem>> {
    val result = List(20) { mutableStateListOf<TimeTableItem>() }
    try {
//        val originalData = getCourseInfoFromCommunity()
        // TODO
    } catch (e : Exception) {
        e.printStackTrace()
    }
    return result
}

private fun parseTime(time : Int) : String {
    val hour = time / 100
    val minute = time % 100
    return "${parseTimeItem(hour)}:${parseTimeItem(minute)}"
}




@Composable
fun NewTimeTableUI(
    items: List<List<TimeTableItem>>,
    week : Int,
    showAll: Boolean,
    innerPadding : PaddingValues,
    modifier: Modifier = Modifier,
    squareModifier : Modifier = Modifier,
    shaderState : ShaderState? = null,
    onSquareClick : (List<TimeTableItem>) -> Unit,
) {
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val calendarSquareHeight by DataStoreManager.calendarSquareHeightNew.collectAsState(initial = MyApplication.CALENDAR_SQUARE_HEIGHT_NEW)
    val enableMergeSquare by DataStoreManager.enableMergeSquare.collectAsState(initial = false)

    val list = if(week >= items.size || week > MyApplication.MAX_WEEK) {
        Exception("NewTimeTableUI received week out of bounds for length ${items.size} of items[${week-1}]").printStackTrace()
        emptyList()
    }  else {
        items[week-1]
    }
    val lineHeight = if(!showAll) 19.sp else 16.sp
    val timeTextSize = if(!showAll) 12.sp else 10.sp
    val textSize = if(!showAll) 13.sp else 11.sp
    val placeTextSize = if(!showAll) 12.sp else 10.sp
    val hasBackground = shaderState != null

    val earliestTime = list.minOfOrNull { it.startTime }?.substringBefore(":")?.toIntOrNull() ?: 8
    val startHour = minOf(earliestTime,8)

    if(enableMergeSquare) {
        Timetable(
            items = list,
            modifier = modifier,
            showAll = showAll,
            showLine = !hasBackground,
            innerPadding = innerPadding,
            hourHeight = calendarSquareHeight.dp,
            startHour = startHour
        ) { list ->
            val color : Pair<Color,Color> = if(!hasBackground) {
                when {
                    list.size > 1 -> Pair(MaterialTheme.colorScheme.errorContainer,MaterialTheme.colorScheme.onErrorContainer.copy(.6f))
                    else -> Pair(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.onPrimaryContainer.copy(.6f))
                }
            } else {
                Pair(MaterialTheme.colorScheme.surfaceContainer,MaterialTheme.colorScheme.onSurface.copy(.6f))
            }
            Surface(
                color = if(!hasBackground) color.first else Color.Transparent,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = squareModifier
                    .let {
                        if(hasBackground) {
                            it
                                .clip(MaterialTheme.shapes.extraSmall)
                                .let {
                                    if(AppVersion.CAN_SHADER) {
                                        it.calendarSquareGlass(
                                            shaderState,
                                            color.first.copy(customBackgroundAlpha),
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
                        if(!hasBackground) {
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
                        if(!hasBackground && list.size == 1) {
                            val item = list[0]
                            when(item.type) {
                                TimeTableType.COURSE -> {
                                    it.containerShare(AppNavRoute.CourseDetail.withArgs(item.name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item}"), MaterialTheme.shapes.extraSmall)
                                }
                                TimeTableType.FOCUS -> {
                                    item.id?.let { id ->
                                        it.containerShare(AppNavRoute.AddEvent.withArgs(id, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item}"), MaterialTheme.shapes.extraSmall)
                                    } ?: it
                                }
                                TimeTableType.EXAM -> {
                                    it
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
                    if(list.size == 1) {
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
                                text = item.name,
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
                                fontSize = placeTextSize,
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
                    } else if(list.size > 1){
                        val startTime = list.minOf { it.startTime }
                        val endTime = list.maxOf { it.endTime }
                        val courses = list.joinToString(",") { it.name.substring(0,1) }

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
                                text = "${list.size}节课冲突",
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
            startHour = startHour
        ) { item ->
            val color : Pair<Color,Color> = if(!hasBackground) {
                when(item.type) {
                    TimeTableType.FOCUS -> Pair(MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.primaryContainer)
                    TimeTableType.COURSE -> Pair(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.onPrimaryContainer.copy(.6f))
                    TimeTableType.EXAM -> Pair(MaterialTheme.colorScheme.errorContainer,MaterialTheme.colorScheme.onErrorContainer.copy(.6f))
                }
            } else {
                Pair(MaterialTheme.colorScheme.surfaceContainer,MaterialTheme.colorScheme.onSurface.copy(.6f))
            }
            Surface(
                color = if(!hasBackground) color.first else Color.Transparent,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = squareModifier
                    .let {
                        if(hasBackground) {
                            it
                                .clip(MaterialTheme.shapes.extraSmall)
                                .let {
                                    if(AppVersion.CAN_SHADER) {
                                        it.calendarSquareGlass(
                                            shaderState,
                                            color.first.copy(customBackgroundAlpha),
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
                        if(!hasBackground) {
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
                        if(!hasBackground) {
                            when(item.type) {
                                TimeTableType.COURSE -> {
                                    it.containerShare(AppNavRoute.CourseDetail.withArgs(item.name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item}"), MaterialTheme.shapes.extraSmall)
                                }
                                TimeTableType.FOCUS -> {
                                    item.id?.let { id ->
                                        it.containerShare(AppNavRoute.AddEvent.withArgs(id, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item}"), MaterialTheme.shapes.extraSmall)
                                    } ?: it
                                }
                                TimeTableType.EXAM -> {
                                    it
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
                            text = item.name,
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
                                fontSize = placeTextSize,
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

@Composable
fun TimeTableDetail(
    bean : List<TimeTableItem>
) {
    if(bean.isEmpty()) {
        return
    }
    Column {
        HazeBottomSheetTopBar("详情", isPaddingStatusBar = false)
        LazyColumn {
            items(bean.size,key = { it }) { index ->
                val data = bean[index]
                with(data) {
                    CardListItem(
                        headlineContent = { Text(name) },
                        supportingContent = {
                            place?.let { Text(it) }
                        },
                        overlineContent = {
                            Text("周"+ numToChinese(dayOfWeek) + " " + startTime + " ~ "  +endTime )
                        },
                        trailingContent = {
                            Text(type.description)
                        },
                        leadingContent = {
                            Icon(painterResource(type.icon),null)
                        },
                        modifier = Modifier.clickable {
                            showToast("正在开发")
                        }
                    )
                }
            }
            item {
                Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
            }
        }
    }
}
@Composable
fun Modifier.drawLineTimeTable(
    columnCount : Float,
    hourPx: Float,
    startHour: Int ,
    zipTime: List<Pair<Float, Float>>,
    zipTimeFactor: Float,
    showAll : Boolean,
    everyPadding : Dp,
    dividerColor : Color = DividerDefaults.color,
    dashEffect : PathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
    ): Modifier {
//    var now by remember { mutableStateOf(LocalTime.now()) }
//    var nowDate by remember { mutableStateOf(LocalDate.now()) }
//    LaunchedEffect(Unit) {
//        while (true) {
//            now = LocalTime.now()
//            nowDate = LocalDate.now()
//            delay(60_000) // 每30秒刷新一次，分钟级足够
//        }
//    }
    return this.drawBehind {
        val w = size.width
        val h = size.height
        // 虚线在列边界
        for (i in 0..columnCount.toInt()) {
            val x = w * i / columnCount.toFloat()
            drawLine(
                color = dividerColor,
                strokeWidth = 1.dp.toPx(),
                start = Offset(x, 0f),
                end = Offset(x, h),
                pathEffect = dashEffect
            )
        }
//        // 绘制当前时间线
//        val nowFloat = now.hour + now.minute / 60f
//        val nowY = timeToY(nowFloat, hourPx, startHour, zipTime, zipTimeFactor)
//
//        if (nowY in 0f..h) {
//            // 获取当前星期（周一=1，周日=7）
//            val today = nowDate.dayOfWeek.value
//            // 计算列宽
//            val columnWidth = w / columnCount
//            // 根据 showAll 判定当前列索引
//            val dayIndex =
//                if (showAll) (today - 1).coerceIn(0, 6)
//                else (today - 1).coerceIn(0, 4)
//            // 当前列的起点与终点 X
//            val startX = columnWidth * dayIndex
//            val endX = columnWidth * (dayIndex + 1)
//
//            drawLine(
//                color = dividerColor,
//                start = Offset(startX, nowY),
//                end = Offset(endX, nowY),
//                strokeWidth = 1.dp.toPx()
//            )
//            // 小圆点
//            val radius = everyPadding.toPx()
//            drawCircle(
//                color = dividerColor,
//                radius = radius,
//                center = Offset(startX, nowY)
//            )
//            drawCircle(
//                color = dividerColor,
//                radius = radius,
//                center = Offset(endX, nowY)
//            )
//        }
    }
}
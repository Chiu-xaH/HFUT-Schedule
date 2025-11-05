package com.hfut.schedule.ui.screen.home.calendar.timetable

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.ShowTeacherConfig
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.calendarSquareGlass
import com.hfut.schedule.ui.screen.home.calendar.common.dateToWeek
import com.hfut.schedule.ui.screen.home.calendar.common.examToCalendar
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCoursesFromCommunity
import com.xah.mirror.util.ShaderState
import com.xah.transition.component.containerShare
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.clickableWithScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

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
    val teacher : String? = null,
    val id : Int? = null,
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
suspend fun allToTimeTableData(context: Context,friendStudentId: String?): List<List<TimeTableItem>> = withContext(Dispatchers.Default) {
    // 并发调用三个数据源
    val jxglstuDeferred = async { communityToTimeTableData(friendStudentId) }
    if(friendStudentId != null) {
        return@withContext jxglstuDeferred.await()
    }
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

private suspend fun jxglstuToTimeTableData(context: Context): List<List<TimeTableItem>> {
    val json = LargeStringDataManager.read(context, LargeStringDataManager.DATUM)
        ?: return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }

        val datumResponse = Gson().fromJson(json, DatumResponse::class.java)
        val scheduleList = datumResponse.result.scheduleList
        val lessonList = datumResponse.result.lessonList
        // 根据id得到课程名
        val courseNameMap = mutableMapOf<String, String>()
        val multiTeacherMap = mutableMapOf<String, Int>()
        for(item in lessonList) {
            courseNameMap[item.id] = item.courseName
            multiTeacherMap[item.id] = item.teacherAssignmentList.size
        }
        val enableCalendarShowTeacher = DataStoreManager.enableCalendarShowTeacher.first()

        for(item in scheduleList) {
            val list = result[item.weekIndex-1]
            val teacher = when(enableCalendarShowTeacher) {
                ShowTeacherConfig.ALL.code -> item.personName
                ShowTeacherConfig.ONLY_MULTI.code -> {
                    multiTeacherMap[item.lessonId.toString()]?.let { size ->
                        if(size > 1) {
                            item.personName
                        } else {
                            null
                        }
                    }
                }
                else -> null
            }
            list.add(TimeTableItem(
                teacher = teacher,
                type = TimeTableType.COURSE,
                name = item.lessonId.toString().let { courseNameMap[it] ?: it },
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
        return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    }
}

private suspend fun focusToTimeTableData(): List<List<TimeTableItem>> {
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

private suspend fun examToTimeTableData(context : Context): List<List<TimeTableItem>> {
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

private suspend fun communityToTimeTableData(friendStudentId : String? = null) : List<List<TimeTableItem>> {
    val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }
    try {
        val enableCalendarShowTeacher = DataStoreManager.enableCalendarShowTeacher.first()

        repeat(MyApplication.MAX_WEEK) { week ->
            val originalData = getCoursesFromCommunity(targetWeek = week+1, friendUserName = friendStudentId)
            val list = result[week]
            repeat(7) { weekday ->
                val data = originalData[weekday].flatMap { it }
                for(item in data) {
                    val time = item.classTime.split("-")
                    if(time.size != 2) {
                        continue
                    }
                    val teacher = when(enableCalendarShowTeacher) {
                        ShowTeacherConfig.ALL.code -> item.teacher
                        else -> null
                    }
                    list.add(TimeTableItem(
                        teacher = teacher,
                        type = TimeTableType.COURSE,
                        name = item.name,
                        dayOfWeek = weekday+1,
                        startTime = time[0],
                        endTime = time[1],
                        place  = item.place?.replace("学堂",""),
                    ))
                }
            }
        }
    } catch (e : Exception) {
        e.printStackTrace()
    }
    for(t in result) {
        val uniqueItems = t.distinctBy {
            it.name + it.place + it.dayOfWeek + it.startTime + it.endTime
        }
        t.clear()
        t.addAll(uniqueItems)
    }
    return result
}

private fun parseTime(time : Int) : String {
    val hour = time / 100
    val minute = time % 100
    return "${parseTimeItem(hour)}:${parseTimeItem(minute)}"
}
// 早八
const val DEFAULT_START_TIME = 8f
const val DEFAULT_END_TIME = 24f
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
    val calendarSquareTextSize by DataStoreManager.calendarSquareTextSize.collectAsState(initial = 1f)
    val calendarSquareTextPadding by DataStoreManager.calendarSquareTextPadding.collectAsState(initial = 1f)

    val list = if(week >= items.size || week > MyApplication.MAX_WEEK) {
        Exception("NewTimeTableUI received week out of bounds for length ${items.size} of items[${week-1}]").printStackTrace()
        emptyList()
    }  else {
        items[week-1]
    }
    val lineHeight = (if(!showAll) 19.sp else 16.sp) * calendarSquareTextPadding
    val textSize = (if(!showAll) 13.sp else 11.sp) * calendarSquareTextSize
    val timeTextSize = (textSize.value-1).sp
    val hasBackground = shaderState != null

    val earliestTime = list.minOfOrNull { it.startTime }
    val startTime = earliestTime?.let {
        minOf(parseTimeToFloat(it),DEFAULT_START_TIME)
    } ?: DEFAULT_START_TIME
    if(enableMergeSquare) {
        Timetable(
            items = list,
            modifier = modifier,
            showAll = showAll,
            showLine = !hasBackground,
            innerPadding = innerPadding,
            hourHeight = calendarSquareHeight.dp,
            startTime = startTime
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
                                    it.containerShare(AppNavRoute.CourseDetail.withArgs(item.name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"), MaterialTheme.shapes.extraSmall)
                                }
                                TimeTableType.FOCUS -> {
                                    item.id?.let { id ->
                                        it.containerShare(AppNavRoute.AddEvent.withArgs(id, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"), MaterialTheme.shapes.extraSmall)
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
                                    it.containerShare(AppNavRoute.CourseDetail.withArgs(item.name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"), MaterialTheme.shapes.extraSmall)
                                }
                                TimeTableType.FOCUS -> {
                                    item.id?.let { id ->
                                        it.containerShare(AppNavRoute.AddEvent.withArgs(id, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"), MaterialTheme.shapes.extraSmall)
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
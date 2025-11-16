package com.hfut.schedule.receiver.widget.focus

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hfut.schedule.R
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.enumeration.BottomBarItems
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.ENDED
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.NOT_STARTED
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.ONGOING
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.formatterTime_HH_MM
import com.hfut.schedule.logic.util.sys.datetime.isHoliday
import com.hfut.schedule.logic.util.sys.datetime.isHolidayTomorrow
import com.hfut.schedule.receiver.widget.util.WidgetCardListItem
import com.hfut.schedule.receiver.widget.util.WidgetTheme
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.focus.funiction.getJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.getTodayJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.getTomorrowJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import com.hfut.schedule.ui.screen.home.texts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class FocusWidgetReceiver :  GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = FocusWidget()
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // 第一个 Widget 被添加到桌面时调用
        CoroutineScope(Dispatchers.Default).launch {
            launch {
                async { RefreshFocusWidgetWorker.startPeriodicWork(context) }.await()
//                RefreshFocusWidgetWorker.getStatus(context)
            }
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        // 最后一个 Widget 被移除时调用
        CoroutineScope(Dispatchers.Default).launch {
            launch {
                async { RefreshFocusWidgetWorker.stopPeriodicWork(context) }.await()
//                RefreshFocusWidgetWorker.getStatus(context)
            }
        }
    }
}

suspend fun refreshFocusWidget(context: Context) = withContext(Dispatchers.IO) {
    FocusWidget().refresh(context)
}

suspend fun hasFocusWidget(context : Context) : Int = withContext(Dispatchers.Default) {
    val glanceManager = GlanceAppWidgetManager(context)
    val glanceIds = glanceManager.getGlanceIds(FocusWidget::class.java)
    return@withContext glanceIds.size
}

val widgetPadding = 5.dp

class RefreshWidgetAction() : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        withContext(Dispatchers.IO) {
            refreshFocusWidget(context)
        }
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "已刷新", Toast.LENGTH_SHORT).show()
        }
    }
}

class OpenMainWithFlagsAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        context.startActivity(intent)
    }
}

class FocusWidget : GlanceAppWidget() {

    suspend fun refresh(context: Context) = withContext(Dispatchers.IO) {
        this@FocusWidget.updateAll(context)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val textSize = DataStoreManager.focusWidgetTextSize.first()
        provideContent {
            WidgetTheme {
                MyContent(textSize)
            }
        }
    }


    private fun getTimeNow() = LocalDateTime.now().format(formatterTime_HH_MM)

    @Composable
    private fun MyContent(textSize : Float) {
        val context = LocalContext.current
        val isDarkTheme = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val courseDataSource =  prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code)
        var todayJxglstuList by remember { mutableStateOf<List<JxglstuCourseSchedule>>(emptyList()) }
        var todayCourseList by remember { mutableStateOf<List<courseDetailDTOList>>(emptyList()) }
        var lastTime by remember { mutableStateOf("00:00") }
        var tomorrowJxglstuList by remember { mutableStateOf<List<JxglstuCourseSchedule>>(emptyList()) }
        var tomorrowCourseList by remember { mutableStateOf<List<courseDetailDTOList>>(emptyList()) }
        var jxglstuLastTime by remember { mutableStateOf("00:00") }
        val specialWorkToday by produceState<String?>(initialValue = null) {
            value = withContext(Dispatchers.IO) {
                DataBaseManager.specialWorkDayDao.searchToday()
            }
        }
        val specialWorkTomorrow by produceState<String?>(initialValue = null) {
            value = withContext(Dispatchers.IO) {
                DataBaseManager.specialWorkDayDao.searchTomorrow()
            }
        }

        val timeNow by produceState(initialValue = getTimeNow()) {
            while (true) {
                value = getTimeNow()
                delay(60_000L) // 每分钟更新一次
            }
        }


        var size by remember { mutableIntStateOf(0) }
        var showTomorrow by remember { mutableStateOf(true) }

        LaunchedEffect(specialWorkToday,specialWorkTomorrow) {
            // 初始化UI数据
            launch(Dispatchers.IO) {
                when(courseDataSource) {
                    CourseType.COMMUNITY.code -> {
                        val weekDayTomorrow = DateTimeManager.dayWeek + 1
                        var weekdayToday = DateTimeManager.dayWeek
                        var nextWeek = DateTimeManager.weeksBetween.toInt()
                        //当今天为周日时，变0为7
                        //当第二天为下一周的周一时，周数+1
                        when(weekDayTomorrow) { 1 -> nextWeek += 1 }
                        when (weekdayToday) { 0 -> weekdayToday = 7 }
                        //判断是否上完今天的课
                        val week = DateTimeManager.weeksBetween.toInt()
                        todayCourseList = getCourseInfoFromCommunity(weekdayToday,week).flatten().distinct()
                        val lastCourse = todayCourseList.lastOrNull()
                        lastTime = lastCourse?.classTime?.substringAfter("-") ?: "00:00"
                        tomorrowCourseList = getCourseInfoFromCommunity(weekDayTomorrow,nextWeek).flatten().distinct()
                        showTomorrow = DateTimeManager.compareTime(lastTime,timeNow) != NOT_STARTED
                        if (showTomorrow) {
                            if(!isHolidayTomorrow()) {
                                size = tomorrowCourseList.size
                            }
                        } else {
                            if(!isHoliday()) {
                                size = todayCourseList.size
                            }
                        }
                    }
                    CourseType.JXGLSTU.code -> {
                        todayJxglstuList = specialWorkToday?.let { getJxglstuCourse(it, context ) } ?: getTodayJxglstuCourse(context)
                        tomorrowJxglstuList = specialWorkTomorrow?.let { getJxglstuCourse(it,context) } ?: getTomorrowJxglstuCourse(context)
                        val jxglstuLastCourse = todayJxglstuList.lastOrNull()
                        jxglstuLastTime = jxglstuLastCourse?.time?.end?.let {
                            parseTimeItem(it.hour) +  ":" + parseTimeItem(it.minute)
                        } ?: "00:00"
                        showTomorrow = DateTimeManager.compareTime(jxglstuLastTime,timeNow) != NOT_STARTED
                        if (showTomorrow) {
                            if(!isHolidayTomorrow()) {
                                size = tomorrowJxglstuList.size
                            }
                        } else {
                            if(!isHoliday()) {
                                size = todayJxglstuList.size
                            }
                        }
                    }
                }
            }
        }


        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(14.dp)
                .clickable(onClick = actionRunCallback<OpenMainWithFlagsAction>())
        ) {
            if(size == 0) {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "近期无课程",
                        modifier = GlanceModifier
                            .clickable(onClick = actionRunCallback<OpenMainWithFlagsAction>()),
                        style = TextStyle(
                            color = GlanceTheme.colors.outline,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                Column() {
                    Box(
                        modifier = GlanceModifier
                            .padding(top = (widgetPadding.value*1.5).dp, start = widgetPadding*2,end = widgetPadding*2, bottom = widgetPadding)
                            .clickable(onClick = actionRunCallback<RefreshWidgetAction>()),

                        ) {
                        // 左侧文本
                        Text(
                            text = texts(BottomBarItems.FOCUS),
                            maxLines = 1,
                            modifier = GlanceModifier
                                .fillMaxWidth(),
                            style = TextStyle(
                                color = GlanceTheme.colors.primary,
                                textAlign = TextAlign.Start
                            )
                        )

                        // 右侧文本
                        Text(
                            text = "${size}节课",
                            maxLines = 1,
                            modifier = GlanceModifier
                                .fillMaxWidth(),
                            style = TextStyle(
                                color = GlanceTheme.colors.outline,
                                textAlign = TextAlign.End
                            )
                        )
                    }

                    LazyVerticalGrid (
                        gridCells = GridCells.Fixed(if(size <= 2) 1 else 2),
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .padding(start = widgetPadding*2,end = widgetPadding*2)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when(courseDataSource) {
                            CourseType.COMMUNITY.code -> {
                                if (showTomorrow) {
                                    if(!isHolidayTomorrow()) {
                                        items(tomorrowCourseList.size) { index ->
                                            val item = tomorrowCourseList[index]
                                            WidgetCardListItem(
                                                textSize = textSize,
                                                headlineText = item.name,
                                                supportingText = item.place,
                                                overlineText = item.classTime,
                                                leadingContent = {
                                                    Image(
                                                        provider = ImageProvider(if (isDarkTheme) R.drawable.exposure_plus_1 else R.drawable.exposure_plus_1_widget),
                                                        contentDescription = null
                                                    )
                                                },
                                                modifier = GlanceModifier.padding(
                                                    end = if (index % 2 == 0) widgetPadding else 0.dp,
                                                    bottom = widgetPadding
                                                )
                                                    .clickable(onClick = actionRunCallback<OpenMainWithFlagsAction>())

                                            )
                                        }
                                    }
                                } else {
                                    if(!isHoliday()) {
                                        items(todayCourseList.size) { index ->
                                            val item = todayCourseList[index]
                                            val time = item.classTime
                                            val startTime = time.substringBefore("-")
                                            val endTime = time.substringAfter("-")
                                            val state = DateTimeManager.getTimeState(startTime, endTime,timeNow)
                                            WidgetCardListItem(
                                                textSize = textSize,
                                                textDecoration = if (state == ENDED) TextDecoration.LineThrough else TextDecoration.None,
                                                headlineText = item.name,
                                                supportingText = item.place,
                                                overlineText = item.classTime,
                                                leadingContent = {
                                                    Image(
                                                        provider = ImageProvider(
                                                            when (state) {
                                                                NOT_STARTED -> {
                                                                    if (isDarkTheme) R.drawable.schedule else R.drawable.schedule_widget
                                                                }

                                                                ONGOING -> {
                                                                    if (isDarkTheme) R.drawable.progress_activity else R.drawable.progress_activity_widget
                                                                }

                                                                ENDED -> {
                                                                    if (isDarkTheme) R.drawable.check else R.drawable.check_widget
                                                                }
                                                            }
                                                        ), contentDescription = null
                                                    )
                                                },
                                                modifier = GlanceModifier.padding(
                                                    end = if (index % 2 == 0) widgetPadding else 0.dp,
                                                    bottom = widgetPadding
                                                )
                                                    .clickable(onClick = actionRunCallback<OpenMainWithFlagsAction>())

                                            )
                                        }
                                    }
                                }
                            }
                            CourseType.JXGLSTU.code -> {
                                if (showTomorrow) {
                                    if(!isHolidayTomorrow()) {
                                        items(tomorrowJxglstuList.size) { index ->
                                            val item = tomorrowJxglstuList[index]
                                            val time = item.time
                                            val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
                                            val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
                                            WidgetCardListItem(
                                                textSize = textSize,
                                                headlineText = item.courseName,
                                                supportingText = item.place,
                                                overlineText = "$startTime-$endTime",
                                                leadingContent = {
                                                    Image(
                                                        provider = ImageProvider(if (isDarkTheme) R.drawable.exposure_plus_1 else R.drawable.exposure_plus_1_widget),
                                                        contentDescription = null
                                                    )
                                                },
                                                modifier = GlanceModifier.padding(
                                                    end = if (index % 2 == 0) widgetPadding else 0.dp,
                                                    bottom = widgetPadding
                                                )
                                                    .clickable(onClick = actionRunCallback<OpenMainWithFlagsAction>())

                                            )
                                        }
                                    }
                                } else {
                                    if(!isHoliday()) {
                                        items(todayJxglstuList.size) { index ->
                                            val item = todayJxglstuList[index]
                                            val time = item.time
                                            val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
                                            val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
                                            val state = DateTimeManager.getTimeState(startTime, endTime,timeNow)
                                            WidgetCardListItem(
                                                textSize = textSize,
                                                textDecoration = if (state == ENDED) TextDecoration.LineThrough else TextDecoration.None,
                                                headlineText = item.courseName,
                                                supportingText = item.place,
                                                overlineText = "$startTime-$endTime",
                                                leadingContent = {
                                                    Image(
                                                        provider = ImageProvider(
                                                            when (state) {
                                                                NOT_STARTED -> {
                                                                    if (isDarkTheme) R.drawable.schedule else R.drawable.schedule_widget
                                                                }

                                                                ONGOING -> {
                                                                    if (isDarkTheme) R.drawable.progress_activity else R.drawable.progress_activity_widget
                                                                }

                                                                ENDED -> {
                                                                    if (isDarkTheme) R.drawable.check else R.drawable.check_widget
                                                                }
                                                            }
                                                        ), contentDescription = null
                                                    )
                                                },
                                                modifier = GlanceModifier.padding(
                                                    end = if (index % 2 == 0) widgetPadding else 0.dp,
                                                    bottom = widgetPadding
                                                )
                                                    .clickable(onClick = actionRunCallback<OpenMainWithFlagsAction>())

                                            )
                                        }
                                    }
                                }
                            }
                            CourseType.NEXT.code -> {}
                        }
                    }
                }
            }
        }
    }
}



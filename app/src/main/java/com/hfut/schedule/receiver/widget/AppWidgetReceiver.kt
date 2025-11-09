package com.hfut.schedule.receiver.widget

import android.content.Context
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.isHoliday
import com.hfut.schedule.logic.util.sys.datetime.isHolidayTomorrow
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.focus.funiction.getJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.getTodayJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.getTomorrowJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppWidgetReceiver :  GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
}

@Composable
fun WidgetTheme(
    content: @Composable () -> Unit
) {
    GlanceTheme (
        content = content
    )
}

class MyAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetTheme {
                MyContent()
            }
        }
    }

    @Composable
    private fun MyContent() {
        val context = LocalContext.current
        val courseDataSource = remember { prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code) }
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
        val showTomorrow = when(courseDataSource) {
            CourseType.COMMUNITY.code -> {
                DateTimeManager.compareTime(lastTime) != DateTimeManager.TimeState.NOT_STARTED
            }
            CourseType.JXGLSTU.code -> {
                DateTimeManager.compareTime(jxglstuLastTime) != DateTimeManager.TimeState.NOT_STARTED
            }
            else -> true
        }

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
                    }
                    CourseType.JXGLSTU.code -> {
                        todayJxglstuList = specialWorkToday?.let { getJxglstuCourse(it, context ) } ?: getTodayJxglstuCourse(context)
                        tomorrowJxglstuList = specialWorkTomorrow?.let { getJxglstuCourse(it,context) } ?: getTomorrowJxglstuCourse(context)
                        val jxglstuLastCourse = todayJxglstuList.lastOrNull()
                        jxglstuLastTime = jxglstuLastCourse?.time?.end?.let {
                            parseTimeItem(it.hour) +  ":" + parseTimeItem(it.minute)
                        } ?: "00:00"
                    }
                    CourseType.NEXT.code -> {}
                }
            }
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(12.dp)
                .clickable {
                    Starter.goToMain(context)
                }
            ,
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid (
                gridCells = GridCells.Fixed(2),
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(start = 6.dp,end = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(2) {
                    Spacer(GlanceModifier.height(6.dp))
                }
                when(courseDataSource) {
                    CourseType.COMMUNITY.code -> {
                        if (showTomorrow) {
                            if(!isHolidayTomorrow()) {
                                items(tomorrowCourseList.size) { index ->
                                    val item = tomorrowCourseList[index]
//                                    Text(item.name)
                                    ListItemCard(
                                        headlineText = item.name,
                                        supportingText = item.place,
                                        overlineText = item.classTime,
                                        leadingContent = {
                                            Image(provider = ImageProvider(R.drawable.exposure_plus_1_widget), contentDescription = null)
                                        },
                                        modifier = GlanceModifier.padding(
                                            end = if(index%2 == 0)6.dp else 0.dp, bottom = 6.dp
                                        )
                                    )
                                }
                            }
                        } else {
                            if(!isHoliday()) {
                                items(todayCourseList.size) { index ->
                                    val item = todayCourseList[index]
//                                    Text(item.name)
                                    ListItemCard(
                                        headlineText = item.name,
                                        supportingText = item.place,
                                        overlineText = item.classTime,
                                        leadingContent = {
                                            Image(provider = ImageProvider(R.drawable.schedule_widget), contentDescription = null)
                                        },
                                        modifier = GlanceModifier.padding(
                                            end = if(index%2 == 0)6.dp else 0.dp, bottom = 6.dp
                                        )
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
                                    ListItemCard(
                                        headlineText = item.courseName,
                                        supportingText = item.place,
                                        overlineText = "$startTime-$endTime",
                                        leadingContent = {
                                            Image(provider = ImageProvider(R.drawable.exposure_plus_1_widget), contentDescription = null)
                                        },
                                        modifier = GlanceModifier.padding(
                                            end = if(index%2 == 0)6.dp else 0.dp, bottom = 6.dp
                                        )
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
                                    ListItemCard(
                                        headlineText = item.courseName,
                                        supportingText = item.place,
                                        overlineText = "$startTime-$endTime",
                                        leadingContent = {
                                            Image(provider = ImageProvider(R.drawable.schedule_widget), contentDescription = null)
                                        },
                                        modifier = GlanceModifier.padding(
                                            end = if(index%2 == 0)6.dp else 0.dp, bottom = 6.dp
                                        )
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



/**
 * 类似 Material3 ListItem 的 Glance 版本
 *
 * 支持插槽：
 * - leadingContent：左侧图标或头像
 * - overlineContent：标题上方的辅助信息
 * - headlineContent：主标题（必传）
 * - supportingContent：副标题
 * - trailingContent：右侧文字或图标
 */
@Composable
fun ListItemCard(
    modifier: GlanceModifier = GlanceModifier,
    leadingContent: (@Composable () -> Unit)? = null,
    overlineText: String? = null,
    headlineText: String,
    supportingText: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: ColorProvider = GlanceTheme.colors.primaryContainer,
) {
    Box (
        modifier = modifier
    ){
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(12.dp)
                .background(colors)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧图标或图片
            if (leadingContent != null) {
                Box(
                    modifier = GlanceModifier.size(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    leadingContent()
                }
                Spacer(modifier = GlanceModifier.width(6.dp))
            }

            // 中间文本区域
            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(end = 6.dp)
            ) {
                if (overlineText != null) {
                    Text(
                        text = overlineText,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        ),
                    )
                    Spacer(modifier = GlanceModifier.height(1.dp))
                }

                Text(
                    maxLines = 1,
                    text = headlineText,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                )

                if (supportingText != null) {
                    Spacer(modifier = GlanceModifier.height(1.dp))
                    Text(
                        maxLines = 1,
                        text = supportingText,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                    )
                }
            }

            // 右侧内容
            if (trailingContent != null) {
                Spacer(modifier = GlanceModifier.width(8.dp))
                trailingContent()
            }
        }
    }
}

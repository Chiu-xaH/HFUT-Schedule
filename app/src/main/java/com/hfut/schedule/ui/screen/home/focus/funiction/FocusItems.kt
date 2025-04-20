package com.hfut.schedule.ui.screen.home.focus.funiction

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.logic.model.Schedule
import com.hfut.schedule.logic.model.community.TodayResponse
import com.hfut.schedule.logic.model.community.TodayResult
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getSchedule
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getTimeStamp
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.sys.DateTimeUtils.TimeState.ENDED
import com.hfut.schedule.logic.util.sys.DateTimeUtils.TimeState.NOT_STARTED
import com.hfut.schedule.logic.util.sys.DateTimeUtils.TimeState.ONGOING
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.hfut.schedule.logic.util.sys.parseToDateTime
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.CustomTextField
import com.hfut.schedule.ui.component.DateRangePickerModal
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LittleDialog
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.RotatingIcon
import com.hfut.schedule.ui.component.ScheduleIcons
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TimeRangePickerDialog
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.cardNormalDp
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.screen.home.calendar.communtiy.getCourseINFO
import com.hfut.schedule.ui.screen.home.cube.apiCheck
import com.hfut.schedule.ui.screen.home.search.function.card.TodayInfo
import com.hfut.schedule.ui.style.ColumnVertical
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScheduleItem(listItem : Schedule, isFuture: Boolean, activity : Activity) {

    if(prefs.getString("my","")?.contains("Schedule") == true) {
        val startTime = listItem.startTime
        val endTime = listItem.endTime

        //判断过期不显示信息
        val startYear = startTime[0]
        val startMonth = startTime[1]
        val startDay = startTime[2]
        var startDateStr = startTime[2].toString()
        var startMonthStr = startTime[1].toString()
        if(startDay < 10) startDateStr = "0$startDay"
        if(startMonth < 10) startMonthStr = "0$startMonth"
        val getStartTime = "${startYear}${startMonthStr}${startDateStr}".toInt()

        val endYear = endTime[0]
        val endMonth = endTime[1]
        val endDay = endTime[2]
        var endDateStr = endTime[2].toString()
        var endMonthStr = endTime[1].toString()
        if(endDay < 10) endDateStr = "0$endDay"
        if(endMonth < 10) endMonthStr = "0$endMonth"
        val getEndTime = "${endYear}${endMonthStr}${endDateStr}".toLong()


        val nowTime = DateTimeUtils.Date_yyyy_MM_dd.replace("-","").toLong()


        if(!isFuture) {
            if(nowTime in getStartTime .. getEndTime)
                ScheduleItemUI(listItem,false,activity)
        }
        else {
            if(nowTime < getStartTime)
                ScheduleItemUI(listItem,true,activity)
        }
    }
}

@Composable
private fun ScheduleItemUI(listItem: Schedule, isFuture : Boolean, activity : Activity) {
    val time = listItem.time
    val info = listItem.info
    val title = listItem.title
    val showPublic = listItem.showPublic

    val itemUI = @Composable {
            StyleCardListItem(
                headlineContent = {  Text(text = title) },
                overlineContent = {Text(text = time)},
                supportingContent = { Text(text = info)},
                leadingContent = { ScheduleIcons(title = title) },
                modifier = Modifier.clickable {},
                trailingContent = {
                    if(isFuture)
                        FilledTonalIconButton(
                            onClick = {
                                try {
                                    val startTime = listItem.startTime
                                    val endTime = listItem.endTime
                                    addToCalendars(startTime,endTime, info, title,time,activity,true)
                                    showToast("添加到系统日历成功")
                                } catch (e : SecurityException) {
                                    showToast("未授予权限")
                                    e.printStackTrace()
                                }
                            }
                        ) {
                            Icon( painterResource(R.drawable.event_upcoming),
                                contentDescription = "Localized description",)
                        }
                }
            )
//        }
    }

    if(prefs.getBoolean("SWITCHMYAPIS", apiCheck())) {
        //正常接受所有来自服务器的信息
        itemUI()
    } else {
        //仅接受showPublic为true
        if(showPublic) {
            itemUI()
        }
    }
}

@Composable
fun NetCourseItem(listItem : Schedule, isFuture: Boolean, activity: Activity) {
    val time = listItem.time
    val info = listItem.info
    val title = listItem.title
    val showPublic = listItem.showPublic


    val itemUI = @Composable {
        if(prefs.getString("my","")?.contains("Schedule") == true) {
            val startTime = listItem.startTime
            val endTime = listItem.endTime

            //判断过期不显示信息
            val endYear = endTime[0]
            val endMonth = endTime[1]
            val endDay = endTime[2]
            var endDateStr = endTime[2].toString()
            var endMonthStr = endTime[1].toString()
            if(endDay < 10) endDateStr = "0$endDay"
            if(endMonth < 10) endMonthStr = "0$endMonth"
            val getEndTime = "${endYear}${endMonthStr}${endDateStr}".toLong()


            val nowTime = DateTimeUtils.Date_yyyy_MM_dd.replace("-","").toLong()


            if(isFuture) {
                if(nowTime < getEndTime) {
                        StyleCardListItem(
                            headlineContent = {  Text(text = title) },
                            overlineContent = { Text(text = time) },
                            supportingContent = { Text(text = info)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.net),
                                    contentDescription = "Localized description",
                                )
                            },
                            trailingContent = {
                                FilledTonalIconButton(
                                    onClick = {
                                        addToCalendars(startTime,endTime, info, title,time,activity)

                                    }
                                ) {
                                    Icon( painterResource(R.drawable.event_upcoming),
                                        contentDescription = "Localized description",)
                                }
                            },
                            modifier = Modifier.clickable { openOperation(info) }
                        )
                }
            } else {
                if(nowTime == getEndTime) {
                        StyleCardListItem(
                            headlineContent = {  Text(text = title) },
                            overlineContent = { Text(text = time) },
                            supportingContent = { Text(text = info)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.net),
                                    contentDescription = "Localized description",
                                )
                            },
                            trailingContent = {
                                Text(text = "今日截止")
                            },
                            modifier = Modifier.clickable { openOperation(info) }
                        )
                }
            }
        }
    }

    if(prefs.getBoolean("SWITCHMYAPIS", apiCheck())) {
        //正常接受所有来自服务器的信息
        itemUI()
    } else {
        //仅接受showPublic为true
        if(showPublic) {
            itemUI()
        }
    }
}

@Composable
fun CommunityTodayCourseItem(index : Int, vm : NetWorkViewModel, hazeState: HazeState, timeNow : String) {

    val switchShowEnded = prefs.getBoolean("SWITCHSHOWENDED",true)

    val week = DateTimeUtils.weeksBetween.toInt()

    var weekday = DateTimeUtils.dayWeek
    if(weekday == 0) weekday = 7
    //课程详情
    val list = getCourseINFO(weekday,week)[index][0]
    var showBottomSheet by remember { mutableStateOf(false) }
    val time = list.classTime

    val startTime = time.substringBefore("-")
    val endTime = time.substringAfter("-")


    val state = DateTimeUtils.getTimeState(startTime, endTime,timeNow)

    if (showBottomSheet) {

        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(list.name, isPaddingStatusBar = false)
            DetailInfos(list,vm = vm, hazeState = hazeState)
        }
    }

    val itemUI = @Composable {
        StyleCardListItem(
            headlineContent = { Text(text = list.name, textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None) },
            overlineContent = { Text(text = time, textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None)},
            supportingContent = { list.place?.let { Text(text = it, textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None) } },
            leadingContent = {
                when(state) {
                    NOT_STARTED -> {
                        Icon(
                            painterResource(R.drawable.schedule),
                            contentDescription = "Localized description",
                        )
                    }
                    ONGOING -> {
                        RotatingIcon(R.drawable.progress_activity)
                    }
                    ENDED -> {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                        )
                    }
                }

            },
            modifier = Modifier.clickable {
                showBottomSheet = true
            },
            trailingContent = {
                Text(
                    when(state) {
                        NOT_STARTED -> "未开始"
                        ONGOING -> "上课中"
                        ENDED -> "已下课"
                    }
                )
            },
        )
    }

    if(switchShowEnded) {
        itemUI()
    } else {
        if(state != ENDED) {
            itemUI()
        }
    }
}

@Composable
fun CommunityTomorrowCourseItem(index : Int, vm: NetWorkViewModel, hazeState: HazeState) {

    val weekdayTomorrow = DateTimeUtils.dayWeek + 1
    var week = DateTimeUtils.weeksBetween.toInt()
    //当第二天为下一周的周一时，周数+1
    when(weekdayTomorrow) {
        1 -> week += 1
    }

    //课程详情
    val list = getCourseINFO(weekdayTomorrow,week)[index][0]
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(list.name, isPaddingStatusBar = false)
            DetailInfos(list,vm = vm, hazeState = hazeState)
        }
    }

    StyleCardListItem(
        headlineContent = { Text(text = list.name) },
        overlineContent = {Text(text = list.classTime)},
        supportingContent = { list.place?.let { Text(text = it) } },
        leadingContent = { Icon(painterResource(R.drawable.exposure_plus_1), contentDescription = "Localized description") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        },
        trailingContent = { Text(text = "明日")}
    )
}

@Composable
fun CustomItem(item : CustomEventDTO,hazeState: HazeState,isFuture: Boolean,activity: Activity,refresh : () -> Unit) {
    val dateTime = item.dateTime
    val nowTimeNum = DateTimeUtils.Date_yyyy_MM_dd.replace("-","").toLong()
    val endNum = with(dateTime.end) { "$year${parseTimeItem(month)}${parseTimeItem(day)}" }.toLong()

    if(item.type == CustomEventType.SCHEDULE) {
        val startNum = with(dateTime.start) { "$year${parseTimeItem(month)}${parseTimeItem(day)}" }.toLong()
        if(nowTimeNum in startNum .. endNum) {
            // 显示在首页
            if(!isFuture)
                CustomItemUI(item, isFuture, activity, hazeState, refresh = refresh)
        } else {
            // 显示在第二页
            if(isFuture)
                // 判断是否过期
                CustomItemUI(item, isFuture, activity, hazeState, isOutOfDate = nowTimeNum > endNum, refresh)
        }
    } else {
        if(endNum == nowTimeNum) {
            // 显示在首页
            if(!isFuture)
                CustomItemUI(item, isFuture, activity, hazeState, refresh = refresh)
        } else {
            // 显示在第二页
            if(isFuture)
                // 判断是否过期
                CustomItemUI(item, isFuture, activity, hazeState, isOutOfDate = nowTimeNum > endNum, refresh = refresh)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomItemUI(item: CustomEventDTO,isFuture: Boolean,activity: Activity,hazeState: HazeState,isOutOfDate : Boolean = false,refresh: () -> Unit) {
    val title = item.title
    val description = item.description
    val dateTime = item.dateTime
    var id by remember { mutableIntStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if(showDialog)
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                if(id > 0) {
                    scope.launch {
                        async { DataBaseManager.customEventDao.del(id) }.await()
                        launch { refresh() }
                        launch { showDialog = false }
                    }
                } else {
                    showToast("id错误")
                }
            },
            dialogText = "要删除此项吗",
            hazeState = hazeState
        )


    StyleCardListItem(
        headlineContent = { Text(text = title, textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        overlineContent = { Text(text = item.remark,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        supportingContent = { description?.let { Text(text = it,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) } },
        leadingContent = {
            Icon(
                painterResource(if(item.type == CustomEventType.SCHEDULE) R.drawable.calendar else R.drawable.net),
                contentDescription = "Localized description",
            )
        },
        trailingContent = {
            ColumnVertical {
                if(isOutOfDate) {
                    FilledTonalIconButton(
                        onClick = {
                            id = item.id
                            showDialog = true
                        }
                    ) { Icon(painterResource(R.drawable.delete), null) }
                } else {
                    if(isFuture) {
                        FilledTonalIconButton(
                            onClick = {
                                addToCalendars(
                                    dateTime,
                                    description,
                                    title,
                                    null,
                                    activity,
                                    item.type == CustomEventType.SCHEDULE
                                )
                            }
                        ) { Icon(painterResource(R.drawable.event_upcoming), null) }
                    } else {
                        if(item.type == CustomEventType.NET_COURSE) {
                            Text("今日截止")
                        }
                    }
                }
                Text("本地")
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = { description?.let { openOperation(it) } },
            onDoubleClick = {
                //双击操作
            },
            onLongClick = {
                //长按操作
                id = item.id
                showDialog = true
            })
    )
}

@Composable
fun TimeStampItem() = BottomTip(getTimeStamp())

@Composable
fun TermTip() = BottomTip(parseSemseter(getSemseter()))

@Composable
fun TodayUI(hazeState: HazeState) {
    var showBottomSheet by  remember { mutableStateOf(false) }

    if (showBottomSheet ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            isFullExpand = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("聚焦通知")
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    TodayInfo()
                }
            }
        }
    }

    //判断明天是否有早八
    val weekdayTomorrow = DateTimeUtils.dayWeek + 1
    var week = DateTimeUtils.weeksBetween.toInt()
    //当第二天为下一周的周一时，周数+1
    when(weekdayTomorrow) {
        1 -> week += 1
    }

    val list = getCourseINFO(weekdayTomorrow,week)
    val time = if(list.size < 1)
        ""
    else list[0][0].classTime.substringBefore(":")

    //判断明天是否需要调休
    var rest by  remember { mutableStateOf(false) }
    val schedule = getSchedule()
    for(element in schedule) {
        if(element.title.contains("调休")) {
            if(element.time.substringBefore(" ") == DateTimeUtils.tomorrow_MM_DD) {
                rest = true
            }
        }
    }

    Box(modifier = Modifier.clickable { showBottomSheet = true }) {
        if( getToday()?.todayExam?.courseName == null &&
            getToday()?.todayCourse?.courseName == null &&
            getToday()?.todayActivity?.activityName == null &&
            getToday()?.bookLending?.bookName == null) {

            TransplantListItem(
                headlineContent = { ScrollText(text = if(rest) "有调休安排" else if( time == "08")"明天有早八" else if(time == "10") "明天有早十"  else if(time == "14" || time == "16" || time == "19" )  "明天睡懒觉" else "明天没有课") },
                overlineContent = { ScrollText(text = "明天") },
                leadingContent = { Icon(painter = painterResource(  if(rest) R.drawable.error else if( time == "08") R.drawable.sentiment_sad else if (time == "10") R.drawable.sentiment_dissatisfied else R.drawable.sentiment_very_satisfied) , contentDescription = "")},
            )
        } else {
            if(getToday()?.todayExam?.courseName != null) {
                TransplantListItem(
                    headlineContent = { ScrollText(text = getToday()?.todayExam?.courseName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.todayExam?.place + "  " + getToday()?.todayExam?.startTime) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = "")},
                )
            }
            if(getToday()?.todayCourse?.courseName != null) {
                TransplantListItem(
                    headlineContent = { ScrollText(text = getToday()?.todayCourse?.courseName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.todayCourse?.place + "  " + getToday()?.todayCourse?.startTime) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "")},
                )
            }
            if(getToday()?.bookLending?.bookName != null) {
                TransplantListItem(
                    headlineContent = { ScrollText(text = getToday()?.bookLending?.bookName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.bookLending?.returnTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.book), contentDescription = "")},
                )
            }
            if(getToday()?.todayActivity?.activityName != null) {
                TransplantListItem(
                    headlineContent = { ScrollText(text = getToday()?.todayActivity?.activityName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.todayActivity?.startTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.schedule), contentDescription = "")},
                )
            }

        }
    }
}

fun getToday() : TodayResult? {
    val json = prefs.getString("TodayNotice","")
    return try {
        val result = Gson().fromJson(json,TodayResponse::class.java).result
        val course = result.todayCourse
        val book = result.bookLending
        val exam = result.todayExam
        val activity = result.todayActivity
        TodayResult(course,book,exam,activity)
    } catch (e : Exception) {
        null
    }
}

fun parseTimeItem(item : Int) : String = item.let {
    if(it < 10) {
        "0$it"
    } else {
        it.toString()
    }
}

@Composable
fun JxglstuTodayCourseItem(item : JxglstuCourseSchedule, vmUI : UIViewModel, hazeState: HazeState, timeNow : String,vm : NetWorkViewModel) {

    val switchShowEnded = prefs.getBoolean("SWITCHSHOWENDED",true)
    //课程详情
    val time = item.time

    val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }


    val state = DateTimeUtils.getTimeState(startTime, endTime,timeNow)
    val name = item.courseName
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet = false
            },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            CourseDetailApi(courseName = name, vm = vm, hazeState = hazeState)
        }
    }

    val itemUI = @Composable {
        StyleCardListItem(
            headlineContent = { Text(text = name, textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None) },
            overlineContent = { Text(text = "$startTime-$endTime", textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None)},
            supportingContent = { Text(text = item.place, textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None) },
            leadingContent = {
                when(state) {
                    NOT_STARTED -> {
                        Icon(
                            painterResource(R.drawable.schedule),
                            contentDescription = "Localized description",
                        )
                    }
                    ONGOING -> {
                        RotatingIcon(R.drawable.progress_activity)
                    }
                    ENDED -> {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                        )
                    }
                }

            },
            modifier = Modifier.clickable {
                showBottomSheet = true
            },
            trailingContent = {
                Text(
                    when(state) {
                        NOT_STARTED -> "未开始"
                        ONGOING -> "上课中"
                        ENDED -> "已下课"
                    }
                )
            },
        )
    }

    if(switchShowEnded) {
        itemUI()
    } else {
        if(state != ENDED) {
            itemUI()
        }
    }
}

@Composable
fun JxglstuTomorrowCourseItem(item : JxglstuCourseSchedule, vmUI : UIViewModel, hazeState: HazeState,vm: NetWorkViewModel) {

    val time = item.time

    val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }

    val name = item.courseName
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet = false
            },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            CourseDetailApi(courseName = name, vm = vm, hazeState = hazeState)
        }
    }

    StyleCardListItem(
        headlineContent = { Text(text = name) },
        overlineContent = {Text(text = "$startTime-$endTime")},
        supportingContent = { Text(text = item.place) },
        leadingContent = { Icon(painterResource(R.drawable.exposure_plus_1), contentDescription = "Localized description") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        },
        trailingContent = { Text(text = "明日")}
    )
}


// 传入今天日期 YYYY-MM-DD 返回当天课程
private fun getJxglstuCourse(date : String,vmUI : UIViewModel) : List<JxglstuCourseSchedule> {
    val list = vmUI.jxglstuCourseScheduleList
    try {
        val bean = date.split("-")
        if(bean.size != 3) return emptyList()

        val currentYear = bean[0].toInt()
        val currentMonth = bean[1].toInt()
        val currentDay = bean[2].toInt()

        return list.filter {
            with(it.time.start) {
                year == currentYear && month == currentMonth && day == currentDay
            }
        }.sortedBy { it.time.start.hour }

    } catch (e : Exception) {
        return emptyList()
    }
}

fun getTodayJxglstuCourse(vmUI : UIViewModel) : List<JxglstuCourseSchedule> = getJxglstuCourse(
    DateTimeUtils.Date_yyyy_MM_dd,vmUI)

fun getTomorrowJxglstuCourse(vmUI : UIViewModel) : List<JxglstuCourseSchedule> = getJxglstuCourse(
    DateTimeUtils.tomorrow_YYYY_MM_DD,vmUI)

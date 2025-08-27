package com.hfut.schedule.ui.screen.home.focus.funiction

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.model.Schedule
import com.hfut.schedule.logic.model.community.TodayResult
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.util.network.ParseJsons.getTimeStamp
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.util.sys.datetime.isHolidayTomorrow
import com.hfut.schedule.logic.util.sys.datetime.isSpecificWorkDayTomorrow
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.ENDED
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.NOT_STARTED
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.ONGOING
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.icon.ScheduleIcons
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.screen.home.search.function.huiXin.card.TodayInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import com.xah.uicommon.style.align.ColumnVertical
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
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


        val nowTime = DateTimeManager.Date_yyyy_MM_dd.replace("-","").toLong()


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

    val scope = rememberCoroutineScope()
    val itemUI = @Composable {
            CardListItem(
                headlineContent = {  Text(text = title) },
                overlineContent = {Text(text = time)},
                supportingContent = { Text(text = info)},
                leadingContent = { ScheduleIcons(title = title) },
                modifier = Modifier.clickable {},
                trailingContent = {
                    if(isFuture)
                        FilledTonalIconButton(
                            onClick = {
                                scope.launch {
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
                            }
                        ) {
                            Icon( painterResource(R.drawable.event_upcoming),
                                contentDescription = "Localized description",)
                        }
                }
            )
//        }
    }

//    if(prefs.getBoolean("SWITCHMYAPIS", apiCheck())) {
        //正常接受所有来自服务器的信息
//        itemUI()
//    } else {
        //仅接受showPublic为true
        if(showPublic) {
            itemUI()
        }
//    }
}

@Composable
fun NetCourseItem(listItem : Schedule, isFuture: Boolean, activity: Activity) {
    val time = listItem.time
    val info = listItem.info
    val title = listItem.title
    val showPublic = listItem.showPublic

    val scope = rememberCoroutineScope()
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


            val nowTime = DateTimeManager.Date_yyyy_MM_dd.replace("-","").toLong()


            if(isFuture) {
                if(nowTime < getEndTime) {
                        CardListItem(
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
                                        scope.launch {
                                            addToCalendars(
                                                startTime,
                                                endTime,
                                                info,
                                                title,
                                                time,
                                                activity
                                            )
                                        }

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
                        CardListItem(
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

//    if(prefs.getBoolean("SWITCHMYAPIS", apiCheck())) {
        //正常接受所有来自服务器的信息
//        itemUI()
//    } else {
        //仅接受showPublic为true
        if(showPublic) {
            itemUI()
        }
//    }
}

@Composable
fun CommunityTodayCourseItem(list : courseDetailDTOList, vm : NetWorkViewModel, hazeState: HazeState, timeNow : String) {

    val switchShowEnded = prefs.getBoolean("SWITCHSHOWENDED",true)


    var weekday = DateTimeManager.dayWeek
    if(weekday == 0) weekday = 7
    //课程详情
//    val list =

    var showBottomSheet by remember { mutableStateOf(false) }
    val time = list.classTime

    val startTime = time.substringBefore("-")
    val endTime = time.substringAfter("-")


    val state = DateTimeManager.getTimeState(startTime, endTime,timeNow)

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
        CardListItem(
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
                        LoadingIcon()
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
fun CommunityTomorrowCourseItem(list: courseDetailDTOList , vm: NetWorkViewModel, hazeState: HazeState) {

    val weekdayTomorrow = DateTimeManager.dayWeek + 1
    var week = DateTimeManager.weeksBetween.toInt()
    //当第二天为下一周的周一时，周数+1
    when(weekdayTomorrow) {
        1 -> week += 1
    }

    //课程详情
//    val list = getCourseInfoFromCommunity(weekdayTomorrow,week)[index][0]
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

    CardListItem(
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
fun CustomItem(item : CustomEventDTO,hazeState: HazeState,isFuture: Boolean,activity: Activity,showTomorrow : Boolean,refresh : () -> Unit) {
    val dateTime = item.dateTime
    val nowTimeNum = (DateTimeManager.Date_yyyy_MM_dd.replace("-","") + DateTimeManager.Time_HH_MM.replace(":","")).toLong()
    val endNum = with(dateTime.end) { "$year${parseTimeItem(month)}${parseTimeItem(day)}${parseTimeItem(hour)}${parseTimeItem(minute)}" }.toLong()
    val startNum = with(dateTime.start) { "$year${parseTimeItem(month)}${parseTimeItem(day)}${parseTimeItem(hour)}${parseTimeItem(minute)}" }.toLong()

    if(item.type == CustomEventType.SCHEDULE) {
        val startNumSummary = with(dateTime.start) { "$year-${parseTimeItem(month)}-${parseTimeItem(day)}" }
        val isTomorrow = DateTimeManager.tomorrow_YYYY_MM_DD == startNumSummary
        if(nowTimeNum in startNum..endNum || isTomorrow && showTomorrow || startNumSummary == DateTimeManager.Date_yyyy_MM_dd) {
            // 显示在首页
            // 显示在首页有三种情况1.日期等于明天（isTomorrow）并且showTomorrow；2.在进行中（nowTimeNum in start .. end）3.未开始但是今天即将开始（startNumSummary==Date.Today）
            if(!isFuture)
                CustomItemUI(item, isFuture, activity, hazeState,isTomorrow = isTomorrow && showTomorrow, refresh = refresh)
        } else {
            // 显示在第二页
            if(isFuture)
                // 判断是否过期
                CustomItemUI(item, isFuture, activity, hazeState, isOutOfDate = nowTimeNum > endNum,isTomorrow = false, refresh)
        }
    } else {
        // 今天截止 并且尚未截止
        if((endNum / 10000 == nowTimeNum  / 10000) && (endNum % 10000 >= nowTimeNum % 10000)) {
            // 显示在首页
            if(!isFuture)
                CustomItemUI(item, isFuture, activity, hazeState,isTomorrow = false, refresh = refresh)
        } else {
            // 显示在第二页
            if(isFuture)
                // 判断是否过期
                CustomItemUI(item, isFuture, activity, hazeState, isTomorrow = false,isOutOfDate = nowTimeNum > endNum, refresh = refresh)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomItemUI(item: CustomEventDTO,isFuture: Boolean,activity: Activity,hazeState: HazeState,isOutOfDate : Boolean = false,isTomorrow : Boolean,refresh: () -> Unit) {
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


    CardListItem(
        headlineContent = { Text(text = title, textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        overlineContent = { Text(text = item.remark,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        supportingContent = { description?.let { Text(text = it,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) } },
        leadingContent = {
            ColumnVertical {
                Icon(
                    painterResource(if(item.type == CustomEventType.SCHEDULE) R.drawable.calendar else R.drawable.net),
                    contentDescription = "Localized description",
                    tint = if(isOutOfDate) LocalContentColor.current.copy(.5f) else LocalContentColor.current
                )
                if(isTomorrow) {
                    Icon(
                        painterResource(R.drawable.exposure_plus_1),
                        contentDescription = "Localized description",
                    )
                }
            }

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
                                scope.launch {
                                    addToCalendars(
                                        dateTime,
                                        description,
                                        title,
                                        null,
                                        activity,
                                        item.type == CustomEventType.SCHEDULE
                                    )
                                }
                            }
                        ) { Icon(painterResource(R.drawable.event_upcoming), null) }
                    } else {
                        if(item.type == CustomEventType.NET_COURSE) {
                            Text("今日截止")
                        }
                    }
                }
                if(isTomorrow) {
                    Text("明天")
                }
                Text(if(isOutOfDate) "过期" else if(item.supabaseId == null)"本地" else "导入")
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
fun TermTip() {
    val v by produceState(initialValue = "") {
        value = parseSemseter(getSemseter())
    }
    BottomTip(v)
}

@Composable
fun TodayUI(hazeState: HazeState,vm: NetWorkViewModel) {
    val data by produceState<TodayResult?>(initialValue = null) {
        onListenStateHolder(vm.todayFormCommunityResponse, onError = { _,_ -> }) { data ->
            value = data
        }
    }
    val noDataUI = @Composable {
        when {
            isHolidayTomorrow() -> {
                TransplantListItem(
                    headlineContent = { ScrollText(text = "节假日休息" ) },
                    overlineContent = { ScrollText(text = "明天") },
                    leadingContent = { Icon(painter = painterResource(R.drawable.sentiment_very_satisfied) , contentDescription = "")},
                )
            }
            isSpecificWorkDayTomorrow() -> {
                TransplantListItem(
                    headlineContent = { ScrollText(text = "有调休上课" ) },
                    overlineContent = { ScrollText(text = "明天") },
                    leadingContent = { Icon(painter = painterResource(R.drawable.warning) , contentDescription = "")},
                    colors = MaterialTheme.colorScheme.errorContainer
                )
            }
            else -> {
                //判断明天是否有早八
                val weekdayTomorrow = DateTimeManager.dayWeek + 1
                var week = DateTimeManager.weeksBetween.toInt()
                //当第二天为下一周的周一时，周数+1
                when(weekdayTomorrow) {
                    1 -> week += 1
                }
                val list = getCourseInfoFromCommunity(weekdayTomorrow,week)
                val time = if(list.isEmpty()) "" else list[0][0].classTime.substringBefore(":")
                TransplantListItem(
                    headlineContent = { ScrollText(text =  if( time == "08")"明天有早八" else if(time == "10") "明天有早十"  else if(time == "14" || time == "16" || time == "19" )  "明天睡懒觉" else "明天没有课") },
                    overlineContent = { ScrollText(text = "今天") },
                    leadingContent = { Icon(painter = painterResource( if( time == "08") R.drawable.sentiment_sad else if (time == "10") R.drawable.sentiment_dissatisfied else R.drawable.sentiment_very_satisfied) , contentDescription = "")},
                )
            }
        }
    }
    if(data == null) {
        noDataUI()
    } else {
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
                        TodayInfo(data!!)
                    }
                }
            }
        }
        Box(modifier = Modifier.clickable { showBottomSheet = true }) {
            with(data!!) {
                if(todayExam.courseName == null && todayCourse.courseName == null && todayActivity.activityName == null && bookLending.bookName == null) {
                    noDataUI()
                } else {
                    with(todayExam) {
                        courseName?.let {
                            TransplantListItem(
                                headlineContent = { ScrollText(text = (it.toString()).replace("学堂","")) },
                                overlineContent = { ScrollText(text = "$place  $startTime") },
                                leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = "")},
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .zIndex(3f)
                            )
                        }
                    }
                    with(todayCourse) {
                        courseName?.let {
                            TransplantListItem(
                                headlineContent = { ScrollText(text =  it.toString()) },
                                overlineContent = { ScrollText(text =  place?.replace("学堂","") + " " +  startTime) },
                                leadingContent = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "")},
                            )
                        }
                    }
                    with(bookLending) {
                        bookName?.let {
                            TransplantListItem(
                                headlineContent = { ScrollText(text =  it.toString()) },
                                overlineContent = { ScrollText(text =  returnTime.toString()) },
                                leadingContent = { Icon(painter = painterResource(R.drawable.book), contentDescription = "")},
                            )
                        }
                    }
                    with(todayActivity) {
                        activityName?.let {
                            TransplantListItem(
                                headlineContent = { ScrollText(text =  it.toString()) },
                                overlineContent = { ScrollText(text =  startTime.toString()) },
                                leadingContent = { Icon(painter = painterResource(R.drawable.schedule), contentDescription = "")},
                            )
                        }
                    }
                }
            }
        }
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
fun JxglstuTodayCourseItem(item : JxglstuCourseSchedule, hazeState: HazeState, timeNow : String,vm : NetWorkViewModel) {

    val switchShowEnded = prefs.getBoolean("SWITCHSHOWENDED",true)
    //课程详情
    val time = item.time

    val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }


    val state = DateTimeManager.getTimeState(startTime, endTime,timeNow)
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
        CardListItem(
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
                        LoadingIcon()
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
fun JxglstuTomorrowCourseItem(item : JxglstuCourseSchedule, hazeState: HazeState,vm: NetWorkViewModel) {

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

    CardListItem(
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
fun getJxglstuCourse(date : String,vmUI : UIViewModel) : List<JxglstuCourseSchedule> {
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
    DateTimeManager.Date_yyyy_MM_dd,vmUI)

fun getTomorrowJxglstuCourse(vmUI : UIViewModel) : List<JxglstuCourseSchedule> = getJxglstuCourse(
    DateTimeManager.tomorrow_YYYY_MM_DD,vmUI)

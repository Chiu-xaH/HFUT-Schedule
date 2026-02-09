package com.hfut.schedule.ui.screen.home.focus.funiction

import android.app.Activity
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.model.Schedule
import com.hfut.schedule.logic.model.community.TodayResult
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.network.repo.hfut.UniAppRepository
import com.hfut.schedule.logic.network.util.MyApiParse.getTimeStamp
import com.hfut.schedule.logic.util.parse.SemesterParser.getSemester
import com.hfut.schedule.logic.util.parse.SemesterParser.parseSemester
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.DateTime
import com.hfut.schedule.logic.util.sys.DateTimeBean
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.ENDED
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.NOT_STARTED
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.TimeState.ONGOING
import com.hfut.schedule.logic.util.sys.datetime.isHolidayTomorrow
import com.hfut.schedule.logic.util.sys.datetime.isSpecificWorkDayTomorrow
import com.hfut.schedule.logic.util.sys.getJxglstuCourseSchedule
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.icon.ScheduleIcons
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.simplifyPlace
import com.hfut.schedule.ui.screen.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.search.function.huiXin.card.TodayInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.containerShare
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.util.LogUtil
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
        var startDateStr = parseTimeItem(startTime[2])
        var startMonthStr = parseTimeItem(startTime[1])
        val getStartTime = "${startYear}${startMonthStr}${startDateStr}".toInt()

        val endYear = endTime[0]
        var endDateStr = parseTimeItem(endTime[2])
        var endMonthStr = parseTimeItem(endTime[1])
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
                                        LogUtil.error(e)
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
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val itemUI = @Composable {
        if(prefs.getString("my","")?.contains("Schedule") == true) {
            val startTime = listItem.startTime
            val endTime = listItem.endTime

            //判断过期不显示信息
            val endYear = endTime[0]
            var endDateStr = parseTimeItem(endTime[2])
            var endMonthStr = parseTimeItem(endTime[1])
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
                            modifier = Modifier.clickable { openOperation(info,context) }
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
                            modifier = Modifier.clickable { openOperation(info,context) }
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
    var week = DateTimeManager.currentWeek.toInt()
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
fun CustomItem(
    item : CustomEventDTO,
    hazeState: HazeState,
    isFuture: Boolean,
    activity: Activity,
    showTomorrow : Boolean,
    navController : NavHostController,
    showOutOfDateItems : Boolean = true,
    refresh : () -> Unit
) {
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
                CustomItemUI(item, isFuture, activity, hazeState,isTomorrow = isTomorrow && showTomorrow, refresh = refresh,navController = navController)
        } else {
            // 显示在第二页
            if(isFuture) {
                // 判断是否过期
                val isOutOfDate = nowTimeNum > endNum
                if(!showOutOfDateItems && isOutOfDate) { } else {
                    CustomItemUI(item, isFuture, activity, hazeState, isOutOfDate = nowTimeNum > endNum,isTomorrow = false, refresh = refresh,navController = navController)
                }
            }
        }
    } else {
        // 今天截止 并且尚未截止
        if((endNum / 10000 == nowTimeNum  / 10000) && (endNum % 10000 >= nowTimeNum % 10000)) {
            // 显示在首页
            if(!isFuture)
                CustomItemUI(item, isFuture, activity, hazeState,isTomorrow = false, refresh = refresh,navController = navController)
        } else {
            // 显示在第二页
            if(isFuture) {
                // 判断是否过期
                val isOutOfDate = nowTimeNum > endNum
                if(!showOutOfDateItems && isOutOfDate) { } else {
                    CustomItemUI(item, isFuture, activity, hazeState, isTomorrow = false,isOutOfDate = nowTimeNum > endNum, refresh = refresh,navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomItemUI(
    item: CustomEventDTO,
    isFuture: Boolean,
    activity: Activity,
    hazeState: HazeState,
    isOutOfDate : Boolean = false,
    navController: NavHostController,
    isTomorrow : Boolean,refresh: () -> Unit
) {
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

    val route = remember { AppNavRoute.AddEvent.withArgs(item.id) }

    CardListItem(
        color = cardNormalColor(),
        cardModifier = Modifier.containerShare(route, MaterialTheme.shapes.medium),
        headlineContent = { Text(text = title, textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        overlineContent = { Text(text = item.remark,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        supportingContent = { description?.let { Text(text = it,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) } },
        leadingContent = {
            ColumnVertical {
                BadgedBox(
                    badge = {
                        if(isTomorrow) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary.copy(0.25f),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text("+1")
                            }
                        }
                    }
                ) {
                    Icon(
                        painterResource(if(item.type == CustomEventType.SCHEDULE) R.drawable.calendar else R.drawable.net),
                        contentDescription = "Localized description",
                        tint = if(isOutOfDate) LocalContentColor.current.copy(.5f) else LocalContentColor.current
                    )
                }
                if(item.supabaseId != null)
                    Text( "导入", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = CARD_NORMAL_DP))
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
                    Text("明日")
                }
//                Text(if(isOutOfDate) "过期" else if(item.supabaseId == null)"本地" else "导入")
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = {
                navController.navigateForTransition(AppNavRoute.AddEvent,route)
            },
            onDoubleClick = {
                showToast("长按删除，单击编辑")
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
        value = parseSemester(getSemester())
    }
    BottomTip(v)
}

@Composable
fun TodayUI(hazeState: HazeState,vm: NetWorkViewModel) {
    val courseDataSource by DataStoreManager.defaultCalendar.collectAsState(initial = CourseType.JXGLSTU.code)

    val data by produceState<TodayResult?>(initialValue = null) {
        onListenStateHolder(vm.todayFormCommunityResponse, onError = { _,_ -> }) { data ->
            value = data
        }
    }
    val context = LocalContext.current
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
//                    colors = MaterialTheme.colorScheme.errorContainer
                )
            }
            else -> {
                //判断明天是否有早八
                val weekdayTomorrow = DateTimeManager.dayWeek + 1
                var week = DateTimeManager.currentWeek.toInt()
                //当第二天为下一周的周一时，周数+1
                when(weekdayTomorrow) {
                    1 -> week += 1
                }
                val time : Int? by produceState(initialValue = null) {
                    value = try {
                        when(courseDataSource) {
                            CourseType.JXGLSTU.code -> {
                                getTomorrowJxglstuCourse().firstOrNull()?.time?.start?.hour
                            }
                            CourseType.COMMUNITY.code -> {
                                val list = getCourseInfoFromCommunity(weekdayTomorrow,week)
                                if(list.isEmpty()) null else list[0][0].classTime.substringBefore(":").toIntOrNull()
                            }
                            CourseType.UNI_APP.code -> {
                                getTomorrowUniAppCourse().firstOrNull()?.time?.start?.hour
                            }
                            else -> null
                        }
                    } catch (e : Exception) {
                        LogUtil.error(e)
                        null
                    }
                }
                val result = when {
                    time == null -> {
                        Pair("无课", R.drawable.sentiment_very_satisfied)
                    }
                    time == 8 -> {
                        Pair("有早八",R.drawable.sentiment_sad)
                    }
                    time == 9 -> {
                        Pair("有早九",R.drawable.sentiment_sad)
                    }
                    time == 10 -> {
                        Pair("有早十",R.drawable.sentiment_dissatisfied)
                    }
                    time == 11 -> {
                        Pair("有早十",R.drawable.sentiment_dissatisfied)
                    }
                    time!! >= 14 -> {
                        Pair("睡懒觉",R.drawable.sentiment_very_satisfied)
                    }
                    else -> {
                        Pair("未知",R.drawable.sentiment_very_satisfied)
                    }
                }

                TransplantListItem(
                    headlineContent = { ScrollText(text = result.first) },
                    overlineContent = { ScrollText(text = "明天") },
                    leadingContent = { Icon(painter = painterResource(result.second) , contentDescription = "")},
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
                        HazeBottomSheetTopBar("聚焦通知", isPaddingStatusBar = false)
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
                                headlineContent = { ScrollText(text = it.toString(), color = MaterialTheme.colorScheme.error) },
                                overlineContent = { ScrollText(text = "${place?.simplifyPlace()} $startTime", color = MaterialTheme.colorScheme.error) },
                                leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = "", tint = MaterialTheme.colorScheme.error)},
                                modifier = Modifier
                                    .background(cardNormalColor())
                                    .zIndex(4f)
                            )
                        }
                    }
                    with(todayCourse) {
                        courseName?.let {
                            TransplantListItem(
                                headlineContent = { ScrollText(text =  it.toString()) },
                                overlineContent = { ScrollText(text =  place?.simplifyPlace() + " " +  startTime) },
                                leadingContent = { Icon(painter = painterResource(R.drawable.schedule), contentDescription = "")},
                                modifier = Modifier
                                    .background(cardNormalColor())
                                    .zIndex(3f)
                            )
                        }
                    }
                    with(bookLending) {
                        bookName?.let {
                            TransplantListItem(
                                headlineContent = { ScrollText(text =  it.toString()) },
                                overlineContent = { ScrollText(text =  returnTime.toString()) },
                                leadingContent = { Icon(painter = painterResource(R.drawable.book), contentDescription = "")},
                                modifier = Modifier
                                    .background(cardNormalColor())
                                    .zIndex(2f)
                            )
                        }
                    }
                    with(todayActivity) {
                        activityName?.let {
                            TransplantListItem(
                                headlineContent = { ScrollText(text =  it.toString()) },
                                overlineContent = { ScrollText(text =  startTime.toString()) },
                                leadingContent = { Icon(painter = painterResource(R.drawable.person_play), contentDescription = "")},
                                modifier = Modifier
                                    .background(cardNormalColor())
                                    .zIndex(1f)
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JxglstuTodayCourseItem(
    index : Int,
    item : JxglstuCourseSchedule,
    switchShowEnded: Boolean,
    timeNow : String,
    navController : NavHostController,
) {
    val time = item.time
    val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val state = DateTimeManager.getTimeState(startTime, endTime,timeNow)
    val name = item.courseName
    val route = AppNavRoute.CourseDetail.withArgs(name,CourseDetailOrigin.FOCUS_TODAY.t + "$index")

    val itemUI = @Composable {
        CardListItem(
            headlineContent = { Text(text = name, textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None) },
            overlineContent = { Text(text = "$startTime-$endTime", textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None)},
            supportingContent = { item.place?.let { Text(text = it, textDecoration = if(state == ENDED) TextDecoration.LineThrough else TextDecoration.None) } },
            cardModifier = Modifier.containerShare(route, MaterialTheme.shapes.medium),
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
                navController.navigateForTransition(AppNavRoute.CourseDetail, route,)
            },
            color = cardNormalColor(),
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JxglstuTomorrowCourseItem(
    index : Int,
    item : JxglstuCourseSchedule,
    navController : NavHostController,
) {
    val time = item.time
    val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val name = item.courseName
    val route = AppNavRoute.CourseDetail.withArgs(name,CourseDetailOrigin.FOCUS_TOMORROW.t + "$index")

    CardListItem(
        headlineContent = { Text(text = name) },
        overlineContent = {Text(text = "$startTime-$endTime")},
        supportingContent = { item.place?.let { Text(text = it) } },
        cardModifier = Modifier.containerShare(route, MaterialTheme.shapes.medium),
        leadingContent = { Icon(painterResource(R.drawable.exposure_plus_1), contentDescription = "Localized description") },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.CourseDetail, route,)
        },
        color = cardNormalColor(),
        trailingContent = { Text(text = "明日")}
    )
}

// 传入今天日期 YYYY-MM-DD 返回当天课程
suspend fun getJxglstuCourse(date : String) : List<JxglstuCourseSchedule> {
    val list = getJxglstuCourseSchedule()
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
        LogUtil.error(e)
        return emptyList()
    }
}

suspend fun getTodayJxglstuCourse() : List<JxglstuCourseSchedule> = getJxglstuCourse(DateTimeManager.Date_yyyy_MM_dd)

suspend fun getTomorrowJxglstuCourse() : List<JxglstuCourseSchedule> = getJxglstuCourse(DateTimeManager.tomorrow_YYYY_MM_DD)

// 传入今天日期 YYYY-MM-DD 返回当天课程
suspend fun getUniAppCourse(date : String) : List<JxglstuCourseSchedule> {
    val list = UniAppRepository.parseUniAppCourses()
    val result = mutableListOf<JxglstuCourseSchedule>()
    try {
        val bean = date.split("-")
        if(bean.size != 3) return emptyList()

        val currentYear = bean[0].toInt()
        val currentMonth = bean[1].toInt()
        val currentDay = bean[2].toInt()

        list.forEach { course ->
            val schedule = course.schedules
            val courseName = course.course.nameZh
            schedule.forEach { item ->
                if(item.date == date) {
                    result.add(JxglstuCourseSchedule(
                        time = DateTime(
                            start = DateTimeBean(
                                year = currentYear,
                                month = currentMonth,
                                day = currentDay,
                                hour = item.startTime / 100,
                                minute = item.startTime % 100
                            ),
                            end = DateTimeBean(
                                year = currentYear,
                                month = currentMonth,
                                day = currentDay,
                                hour = item.endTime / 100,
                                minute = item.endTime % 100
                            )
                        ),
                        place = item.room?.nameZh,
                        courseName = courseName
                    ))
                }
            }
        }

        return result.sortedBy { it.time.start.hour }

    } catch (e : Exception) {
        LogUtil.error(e)
        return emptyList()
    }
}

suspend fun getTodayUniAppCourse() : List<JxglstuCourseSchedule> = getUniAppCourse(DateTimeManager.Date_yyyy_MM_dd)

suspend fun getTomorrowUniAppCourse() : List<JxglstuCourseSchedule> = getUniAppCourse(DateTimeManager.tomorrow_YYYY_MM_DD)

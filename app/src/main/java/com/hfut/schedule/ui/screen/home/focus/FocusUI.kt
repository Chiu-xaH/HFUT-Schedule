package com.hfut.schedule.ui.screen.home.focus

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.enumeration.SortType
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.util.network.ParseJsons.getCustomEvent
import com.hfut.schedule.logic.util.network.ParseJsons.getNetCourse
import com.hfut.schedule.logic.util.network.ParseJsons.getSchedule
import com.hfut.schedule.logic.util.network.toTimestampWithOutT
import com.hfut.schedule.logic.util.sys.datetime.isHoliday
import com.hfut.schedule.logic.util.sys.datetime.isHolidayTomorrow
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.cube.sub.FocusCard
import com.hfut.schedule.ui.screen.home.focus.funiction.CommunityTodayCourseItem
import com.hfut.schedule.ui.screen.home.focus.funiction.CommunityTomorrowCourseItem
import com.hfut.schedule.ui.screen.home.focus.funiction.CustomItem
import com.hfut.schedule.ui.screen.home.focus.funiction.JxglstuTodayCourseItem
import com.hfut.schedule.ui.screen.home.focus.funiction.JxglstuTomorrowCourseItem
import com.hfut.schedule.ui.screen.home.focus.funiction.NetCourseItem
import com.hfut.schedule.ui.screen.home.focus.funiction.ScheduleItem
import com.hfut.schedule.ui.screen.home.focus.funiction.TimeStampItem
import com.hfut.schedule.ui.screen.home.focus.funiction.getJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.getTodayJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.getTomorrowJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.initNetworkRefresh
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.JxglstuExamUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.getExamJXGLSTU
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAB_LEFT = 0
private const val TAB_RIGHT = 1


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TodayScreen(
    vm : NetWorkViewModel,
    vm2 : LoginViewModel,
    innerPadding : PaddingValues,
    vmUI : UIViewModel,
    ifSaved : Boolean,
    state: PagerState,
    hazeState: HazeState,
    sortType: SortType,
    sortReversed : Boolean
) {
    var scheduleList by remember { mutableStateOf(getSchedule()) }
    var netCourseList by remember { mutableStateOf(getNetCourse()) }
    var refreshing by remember { mutableStateOf(false) }
    var timeNow by remember { mutableStateOf(DateTimeManager.Time_HH_MM) }
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async { refreshing = true }.await()
            async { DateTimeManager.updateTime { timeNow = it } }.await()
            async { initNetworkRefresh(vm,vm2,vmUI,ifSaved) }.await()
            launch { netCourseList = getNetCourse() }
            launch { scheduleList = getSchedule() }
            launch { refreshing = false }
        }
    })

    var refreshDB by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()

    val courseDataSource = prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code)

    var lastTime by remember { mutableStateOf("00:00") }
    var tomorrowCourseList by remember { mutableStateOf<List<courseDetailDTOList>>(emptyList()) }
    var todayCourseList by remember { mutableStateOf<List<courseDetailDTOList>>(emptyList()) }
    var jxglstuLastTime by remember { mutableStateOf("00:00") }
    var tomorrowJxglstuList by remember { mutableStateOf<List<JxglstuCourseSchedule>>(emptyList()) }
    var todayJxglstuList by remember { mutableStateOf<List<JxglstuCourseSchedule>>(emptyList()) }

    var customScheduleList by remember { mutableStateOf<List<CustomEventDTO>>(emptyList()) }
    val showFocus by DataStoreManager.showCloudFocusFlow.collectAsState(initial = true)
    val showStorageFocus by DataStoreManager.showFocusFlow.collectAsState(initial = true)

    var specialWorkToday  by remember { mutableStateOf<String?>(null) }
    var specialWorkTomorrow by remember { mutableStateOf<String?>(null) }
    val specialWorkDayChange by remember { derivedStateOf { vmUI.specialWOrkDayChange } }

    LaunchedEffect(specialWorkDayChange) {
        specialWorkToday = DataBaseManager.specialWorkDayDao.searchToday()
        specialWorkTomorrow = DataBaseManager.specialWorkDayDao.searchTomorrow()
    }

    // 初始化
    LaunchedEffect(specialWorkToday,specialWorkTomorrow) {
        // 初始化UI数据
        launch {
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
                    todayJxglstuList = specialWorkToday?.let { getJxglstuCourse(it,vmUI) } ?: getTodayJxglstuCourse(vmUI)
                    tomorrowJxglstuList = specialWorkTomorrow?.let { getJxglstuCourse(it,vmUI) } ?: getTomorrowJxglstuCourse(vmUI)
                    val jxglstuLastCourse = todayJxglstuList.lastOrNull()
                    jxglstuLastTime = jxglstuLastCourse?.time?.end?.let {
                        parseTimeItem(it.hour) +  ":" + parseTimeItem(it.minute)
                    } ?: "00:00"
                }
                CourseType.NEXT.code -> {}
            }
        }
    }
    LaunchedEffect(Unit) {
        // 冷启动
        launch {
            refreshing = true
            initNetworkRefresh(vm,vm2,vmUI,ifSaved)
            refreshing = false
        }
        // 加载数据库
        launch {
            launch { customScheduleList = getCustomEvent() }
        }
        // 一分钟更新时间 触发重组
        launch {
            while (true) {
                delay(1000L*60)
                DateTimeManager.updateTime { timeNow = it }
            }
        }
    }

    val isAddUIExpanded by remember { derivedStateOf { vmUI.isAddUIExpanded } }

    LaunchedEffect(refreshDB,isAddUIExpanded) {
        if(!isAddUIExpanded) {
            launch { customScheduleList = getCustomEvent() }
        }
    }
    LaunchedEffect(sortType,sortReversed) {
        customScheduleList =  when(sortType) {
            SortType.TIME_LINE -> customScheduleList.sortedBy { when(it.type) {
                CustomEventType.NET_COURSE -> it.dateTime.end.toTimestampWithOutT()
                CustomEventType.SCHEDULE -> it.dateTime.start.toTimestampWithOutT()
            } }
            SortType.CREATE_TIME -> customScheduleList.sortedBy { it.id }
        }.let { if (sortReversed) it.reversed() else it }
    }

    Box(modifier = Modifier.fillMaxSize().pullRefresh(states)) {
        //lastTime字符串一定得到HH:MM格式，封装一个函数获取本地时间，再写代码比较两者
        HorizontalPager(state = state) { page ->
            val showTomorrow = when(courseDataSource) {
                CourseType.COMMUNITY.code -> {
                    DateTimeManager.compareTime(lastTime) != DateTimeManager.TimeState.NOT_STARTED
                }
                CourseType.JXGLSTU.code -> {
                    DateTimeManager.compareTime(jxglstuLastTime) != DateTimeManager.TimeState.NOT_STARTED
                }
                else -> true
            }
            Scaffold {
                LazyColumn(state = scrollState) {
                    item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                    when(page) {
                        TAB_LEFT -> {
                            item { FocusCard(vmUI,vm,hazeState) }
                            //课表
                            when(courseDataSource) {
                                CourseType.COMMUNITY.code -> {
                                    if (showTomorrow) {
                                        if(!isHolidayTomorrow()) {
                                            items(tomorrowCourseList.size) { item -> CommunityTomorrowCourseItem(list = tomorrowCourseList[item],vm,hazeState) }
                                        }
                                    } else {
                                        if(!isHoliday()) {
                                            items(todayCourseList.size) { item -> CommunityTodayCourseItem(list = todayCourseList[item],vm, hazeState,timeNow) }
                                        }
                                    }
                                }
                                CourseType.JXGLSTU.code -> {
                                    if (showTomorrow) {
                                        if(!isHolidayTomorrow()) {
                                            tomorrowJxglstuList.let { list ->
                                                items(list.size) { item ->
                                                    JxglstuTomorrowCourseItem(list[item], hazeState,vm)
                                                }
                                            }
                                        }
                                    } else {
                                        if(!isHoliday()) {
                                            todayJxglstuList.let { list ->
                                                items(list.size) { item ->
                                                    JxglstuTodayCourseItem(list[item], hazeState,timeNow,vm)
                                                }
                                            }
                                        }
                                    }
                                }
                                CourseType.NEXT.code -> {}
                            }
                            //日程
                            if(showStorageFocus)
                                customScheduleList.let { list ->
                                    items(list.size){ item ->
                                        activity?.let { it1 ->
                                            CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = false,showTomorrow = showTomorrow) { refreshDB = !refreshDB }
                                        }
                                    }
                                }
                            if(showFocus)
                                scheduleList.let { list ->
                                    items(list.size) { item ->
                                        activity?.let {
                                            ScheduleItem(listItem = list[item],false,it)
                                        }
                                    }
                                }
                            //考试
                            items(getExamJXGLSTU()) { item -> JxglstuExamUI(item,false) }
                            //网课
                            if(showFocus)
                                netCourseList.let { list ->
                                    items(list.size) { item ->
                                        activity?.let {
                                            NetCourseItem(listItem = list[item],false,it)
                                        }
                                    }
                                }
                        }
                        TAB_RIGHT -> {
                            //日程
                            if(showStorageFocus)
                                customScheduleList.let { list ->
                                    items(list.size){ item ->
                                        activity?.let { it1 ->
                                            CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = true,showTomorrow = false) { refreshDB = !refreshDB }
                                        }
                                    }
                                }
                            if(showFocus)
                                scheduleList.let { list ->
                                    items(list.size) { item ->
                                        activity?.let {
                                            ScheduleItem(listItem = list[item],true,it)
                                        }
                                    }
                                }
                            //网课
                            if(showFocus)
                                netCourseList.let { list ->
                                    items(list.size) { item ->
                                        activity?.let {
                                            NetCourseItem(listItem = list[item],true,it)
                                        }
                                    }
                                }

                            //第二天课表
                            if(!isHolidayTomorrow()) {
                                when(courseDataSource) {
                                    CourseType.COMMUNITY.code -> {
                                        if (DateTimeManager.compareTime(lastTime) == DateTimeManager.TimeState.NOT_STARTED) {
                                            items(tomorrowCourseList.size) { item ->
                                                CommunityTomorrowCourseItem(list = tomorrowCourseList[item],vm,hazeState)
                                            }
                                        }
                                    }
                                    CourseType.JXGLSTU.code -> {
                                        if (DateTimeManager.compareTime(jxglstuLastTime) == DateTimeManager.TimeState.NOT_STARTED) {
                                            tomorrowJxglstuList.let { list ->
                                                items(list.size) { item ->
                                                    JxglstuTomorrowCourseItem(list[item], hazeState,vm)
                                                }
                                            }
                                        }
                                    }
                                    CourseType.NEXT.code -> {}
                                }
                            }
                            // API提示文字
                            item { TimeStampItem() }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                }
            }
        }
        RefreshIndicator(refreshing, states, Modifier.padding(innerPadding).align(Alignment.TopCenter))
    }
}


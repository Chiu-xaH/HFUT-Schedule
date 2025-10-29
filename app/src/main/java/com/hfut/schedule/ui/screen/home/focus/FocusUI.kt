package com.hfut.schedule.ui.screen.home.focus

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.enumeration.SortType
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.network.util.MyApiParse.getCustomEvent
import com.hfut.schedule.logic.network.util.MyApiParse.getNetCourse
import com.hfut.schedule.logic.network.util.MyApiParse.getSchedule
import com.hfut.schedule.logic.network.util.toTimestampWithOutT
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.isHoliday
import com.hfut.schedule.logic.util.sys.datetime.isHolidayTomorrow
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
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.getExamFromCache
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getCourseInfoFromCommunity
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAB_LEFT = 0
private const val TAB_RIGHT = 1


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TodayScreen(
    vm : NetWorkViewModel,
    innerPadding : PaddingValues,
    vmUI : UIViewModel,
    ifSaved : Boolean,
    state: PagerState,
    hazeState: HazeState,
    sortType: SortType,
    sortReversed : Boolean,
    navController : NavHostController,
) {
    val context = LocalContext.current
    var scheduleList by remember { mutableStateOf(getSchedule()) }
    var netCourseList by remember { mutableStateOf(getNetCourse()) }
    var refreshing by rememberSaveable { mutableStateOf(true) }
    var timeNow by remember { mutableStateOf(DateTimeManager.Time_HH_MM) }
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshing = true
            async {
                launch { DateTimeManager.updateTime { timeNow = it } }
                launch { initNetworkRefresh(vm, vmUI, ifSaved, context) }
                launch { netCourseList = getNetCourse() }
                launch { scheduleList = getSchedule() }
            }.await()
            refreshing = false
        }
    })

    var refreshDB by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()
    val courseDataSource = remember { prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code) }
    var lastTime by remember { mutableStateOf("00:00") }
    var tomorrowCourseList by remember { mutableStateOf<List<courseDetailDTOList>>(emptyList()) }
    var todayCourseList by remember { mutableStateOf<List<courseDetailDTOList>>(emptyList()) }
    var jxglstuLastTime by remember { mutableStateOf("00:00") }
    var tomorrowJxglstuList by remember { mutableStateOf<List<JxglstuCourseSchedule>>(emptyList()) }
    var todayJxglstuList by remember { mutableStateOf<List<JxglstuCourseSchedule>>(emptyList()) }
    var customScheduleList by remember { mutableStateOf<List<CustomEventDTO>>(emptyList()) }
    val specialWorkDayChange by remember { derivedStateOf { vmUI.specialWOrkDayChange } }
    val specialWorkToday by produceState<String?>(initialValue = null, key1 = specialWorkDayChange) {
        value = withContext(Dispatchers.IO) {
            DataBaseManager.specialWorkDayDao.searchToday()
        }
    }
    val specialWorkTomorrow by produceState<String?>(initialValue = null, key1 = specialWorkDayChange) {
        value = withContext(Dispatchers.IO) {
            DataBaseManager.specialWorkDayDao.searchTomorrow()
        }
    }
    val exams by produceState(initialValue = emptyList()) {
        value = getExamFromCache(context)
    }
    // 加载数据库
    LaunchedEffect(refreshDB) {
        launch { customScheduleList = getCustomEvent() }
    }

    // 初始化
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
    LaunchedEffect(Unit) {
        // 冷启动
        launch {
            // 避免重复加载
            if(refreshing == false) {
                return@launch
            }
            async {
                initNetworkRefresh(vm,vmUI,ifSaved,context)
            }.await()
            refreshing = false
        }
        // 一分钟更新时间 触发重组
        launch {
            while (true) {
                delay(1000L*60)
                DateTimeManager.updateTime { timeNow = it }
            }
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
    val switchShowEnded = remember { prefs.getBoolean("SWITCHSHOWENDED", true) }


    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(states)) {
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
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = scrollState) {
                    item { InnerPaddingHeight(innerPadding,true) }
                    when(page) {
                        TAB_LEFT -> {
                            item { FocusCard(vmUI,vm,hazeState, navController,) }
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
                                                    JxglstuTomorrowCourseItem(item,list[item],navController,)
                                                }
                                            }
                                        }
                                    } else {
                                        if(!isHoliday()) {
                                            todayJxglstuList.let { list ->
                                                items(list.size) { item ->
                                                    JxglstuTodayCourseItem(item,list[item], switchShowEnded,timeNow,navController,)
                                                }
                                            }
                                        }
                                    }
                                }
                                CourseType.NEXT.code -> {}
                            }
                            //日程
                            customScheduleList.let { list ->
                                items(list.size){ item ->
                                    activity?.let { it1 ->
                                        CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = false,showTomorrow = showTomorrow, navController = navController) { refreshDB = !refreshDB }
                                    }
                                }
                            }

                            scheduleList.let { list ->
                                items(list.size) { item ->
                                    activity?.let {
                                        ScheduleItem(listItem = list[item],false,it)
                                    }
                                }
                            }

                            //考试
                            items(exams.size) { index -> JxglstuExamUI(exams[index],false) }
                            //网课
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
                            customScheduleList.let { list ->
                                items(list.size){ item ->
                                    activity?.let { it1 ->
                                        CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = true,showTomorrow = false, navController = navController) { refreshDB = !refreshDB }
                                    }
                                }
                            }

                            scheduleList.let { list ->
                                items(list.size) { item ->
                                    activity?.let {
                                        ScheduleItem(listItem = list[item],true,it)
                                    }
                                }
                            }

                            //网课
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
                                                    JxglstuTomorrowCourseItem(item,list[item],navController,)
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
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
        RefreshIndicator(refreshing, states, Modifier
            .padding(innerPadding)
            .align(Alignment.TopCenter))
    }
}


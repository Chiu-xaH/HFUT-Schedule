package com.hfut.schedule.ui.activity.home.focus.main

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.beans.community.courseDetailDTOList
import com.hfut.schedule.logic.db.entity.CustomEventDTO
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.JxglstuCourseSchedule
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.parse.ParseJsons.getCustomNetCourse
import com.hfut.schedule.logic.utils.parse.ParseJsons.getCustomSchedule
import com.hfut.schedule.logic.utils.parse.ParseJsons.getNetCourse
import com.hfut.schedule.logic.utils.parse.ParseJsons.getSchedule
import com.hfut.schedule.ui.activity.home.calendar.communtiy.getCourseINFO
import com.hfut.schedule.ui.activity.home.calendar.multi.CourseType
import com.hfut.schedule.ui.activity.home.cube.items.subitems.FocusCard
import com.hfut.schedule.ui.activity.home.focus.funictions.CommunityTodayCourseItem
import com.hfut.schedule.ui.activity.home.focus.funictions.CommunityTomorrowCourseItem
import com.hfut.schedule.ui.activity.home.focus.funictions.CustomItem
import com.hfut.schedule.ui.activity.home.focus.funictions.JxglstuTodayCourseItem
import com.hfut.schedule.ui.activity.home.focus.funictions.JxglstuTomorrowCourseItem
import com.hfut.schedule.ui.activity.home.focus.funictions.NetCourseItem
import com.hfut.schedule.ui.activity.home.focus.funictions.ScheduleItem
import com.hfut.schedule.ui.activity.home.focus.funictions.TimeStampItem
import com.hfut.schedule.ui.activity.home.focus.funictions.getTodayJxglstuCourse
import com.hfut.schedule.ui.activity.home.focus.funictions.getTomorrowJxglstuCourse
import com.hfut.schedule.ui.activity.home.focus.funictions.parseTimeItem
import com.hfut.schedule.ui.activity.home.main.initNetworkRefresh
import com.hfut.schedule.ui.activity.home.search.functions.exam.JxglstuExamUI
import com.hfut.schedule.ui.activity.home.search.functions.exam.getExamJXGLSTU
import com.hfut.schedule.ui.utils.components.RefreshIndicator
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAB_LEFT = 0
private const val TAB_RIGHT = 1


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodayScreen(
    vm : NetWorkViewModel,
    vm2 : LoginViewModel,
    innerPadding : PaddingValues,
    vmUI : UIViewModel,
    ifSaved : Boolean,
    webVpn : Boolean,
    state: PagerState,
    hazeState: HazeState,
) {
    var scheduleList by remember { mutableStateOf(getSchedule()) }
    var netCourseList by remember { mutableStateOf(getNetCourse()) }
    var refreshing by remember { mutableStateOf(false) }
    var timeNow by remember { mutableStateOf(DateTimeUtils.Time_HH_MM) }
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
            }.await()
            async { DateTimeUtils.updateTime { timeNow = it } }.await()
            async { initNetworkRefresh(vm,vm2,vmUI,ifSaved) }.await()
            launch { netCourseList = getNetCourse() }
            launch { scheduleList = getSchedule() }
            launch {
                refreshing = false
            }
        }
    })

    var refreshDB by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()
    val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }

    val courseDataSource = prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.COMMUNITY.code)

    var lastTime by remember { mutableStateOf("00:00") }
    var tomorrowCourseList by remember { mutableStateOf<List<List<courseDetailDTOList>>>(emptyList()) }
    var todayCourseList by remember { mutableStateOf<List<List<courseDetailDTOList>>>(emptyList()) }
    var jxglstuLastTime by remember { mutableStateOf("00:00") }
    var tomorrowJxglstuList by remember { mutableStateOf<List<JxglstuCourseSchedule>>(emptyList()) }
    var todayJxglstuList by remember { mutableStateOf<List<JxglstuCourseSchedule>>(emptyList()) }

    var customNetCourseList by remember { mutableStateOf<List<CustomEventDTO>>(emptyList()) }
    var customScheduleList by remember { mutableStateOf<List<CustomEventDTO>>(emptyList()) }

    // 初始化
    LaunchedEffect(Unit) {
        // 初始化UI数据
        launch {
            when(courseDataSource) {
                CourseType.COMMUNITY.code -> {
                    val weekDayTomorrow = DateTimeUtils.dayWeek + 1
                    var weekdayToday = DateTimeUtils.dayWeek
                    var nextWeek = DateTimeUtils.weeksBetween.toInt()
                    //当今天为周日时，变0为7
                    //当第二天为下一周的周一时，周数+1
                    when(weekDayTomorrow) { 1 -> nextWeek += 1 }
                    when (weekdayToday) { 0 -> weekdayToday = 7 }
                    //判断是否上完今天的课
                    val week = DateTimeUtils.weeksBetween.toInt()
                    todayCourseList = getCourseINFO(weekdayToday,week)
                    val lastCourse = todayCourseList.lastOrNull()
                    lastTime = if(lastCourse != null) {
                        lastCourse[0].classTime.substringAfter("-")
                    } else { "00:00" }
                    tomorrowCourseList = getCourseINFO(weekDayTomorrow,nextWeek)
                }
                CourseType.JXGLSTU.code -> {
                    todayJxglstuList = getTodayJxglstuCourse(vmUI)
                    tomorrowJxglstuList = getTomorrowJxglstuCourse(vmUI)
                    val jxglstuLastCourse = todayJxglstuList.lastOrNull()
                    jxglstuLastTime = jxglstuLastCourse?.time?.end?.let {
                        parseTimeItem(it.hour) +  ":" + parseTimeItem(it.minute)
                    } ?: "00:00"
                }
                CourseType.NEXT.code -> {}
            }
        }
        // 冷启动
        launch {
            initNetworkRefresh(vm,vm2,vmUI,ifSaved)
        }
        // 加载数据库
        launch {
            launch { customNetCourseList = getCustomNetCourse() }
            launch { customScheduleList = getCustomSchedule() }
        }
        // 一分钟更新时间 触发重组
        launch {
            while (true) {
                delay(1000L*60)
                DateTimeUtils.updateTime { timeNow = it }
            }
        }
    }
    val isAddUIExpanded by remember { derivedStateOf { vmUI.isAddUIExpanded } }

    LaunchedEffect(refreshDB,isAddUIExpanded) {
        if(!isAddUIExpanded) {
            launch { customNetCourseList = getCustomNetCourse() }
            launch { customScheduleList = getCustomSchedule() }
        }
    }

    Box(modifier = Modifier.fillMaxSize().pullRefresh(states)) {
        //lastTime字符串一定得到HH:MM格式，封装一个函数获取本地时间，再写代码比较两者
        HorizontalPager(state = state) { page ->
            Scaffold {
                LazyColumn(state = scrollState) {
                    item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                    when(page) {
                        TAB_LEFT -> {
                            item { FocusCard(vmUI,vm,hazeState) }
                            //课表
                            when(courseDataSource) {
                                CourseType.COMMUNITY.code -> {
                                    if (DateTimeUtils.compareTime(lastTime) != DateTimeUtils.TimeState.NOT_STARTED)
                                        items(tomorrowCourseList.size) { item -> CommunityTomorrowCourseItem(index = item,vm,hazeState) }
                                    else
                                        items(todayCourseList.size) { item -> CommunityTodayCourseItem(index = item,vm, hazeState,timeNow) }
                                }
                                CourseType.JXGLSTU.code -> {
                                    if (DateTimeUtils.compareTime(jxglstuLastTime) != DateTimeUtils.TimeState.NOT_STARTED)
                                        tomorrowJxglstuList.let { list -> items(list.size) { item -> JxglstuTomorrowCourseItem(list[item],vmUI, hazeState) } }
                                    else
                                        todayJxglstuList.let { list -> items(list.size) { item -> JxglstuTodayCourseItem(list[item],vmUI, hazeState,timeNow) } }
                                }
                                CourseType.NEXT.code -> {}
                            }
                            //日程
                            customScheduleList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = false) { refreshDB = !refreshDB } } } }
                            scheduleList.let { list -> items(list.size) { item -> activity?.let { ScheduleItem(listItem = list[item],false,it) } } }
                            //考试
                            items(getExamJXGLSTU()) { item -> JxglstuExamUI(item,false) }
                            //网课
                            customNetCourseList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1,isFuture = false) { refreshDB = !refreshDB } } } }
                            netCourseList.let { list -> items(list.size) { item -> activity?.let { NetCourseItem(listItem = list[item],false,it) } } }
                        }
                        TAB_RIGHT -> {
                            //日程
                            customScheduleList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = true) { refreshDB = !refreshDB } } } }
                            scheduleList.let { list -> items(list.size) { item -> activity?.let { ScheduleItem(listItem = list[item],true,it) }  } }
                            //网课
                            customNetCourseList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = true) { refreshDB = !refreshDB } } } }
                            netCourseList.let { list -> items(list.size) { item -> activity?.let { NetCourseItem(listItem = list[item],true,it) } } }
                            //第二天课表
                            when(courseDataSource) {
                                CourseType.COMMUNITY.code -> {
                                    if (DateTimeUtils.compareTime(lastTime) == DateTimeUtils.TimeState.NOT_STARTED)
                                        items(tomorrowCourseList.size) { item -> CommunityTomorrowCourseItem(index = item,vm,hazeState) }
                                }
                                CourseType.JXGLSTU.code -> {
                                    if (DateTimeUtils.compareTime(jxglstuLastTime) == DateTimeUtils.TimeState.NOT_STARTED)
                                        tomorrowJxglstuList.let { list -> items(list.size) { item -> JxglstuTomorrowCourseItem(list[item],vmUI, hazeState) } }
                                }
                                CourseType.NEXT.code -> {}
                            }
                            // API提示文字
                            item { TimeStampItem() }
                        }
                    }
                    items(2) { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                }
            }
        }
        RefreshIndicator(refreshing, states, Modifier.padding(innerPadding).align(Alignment.TopCenter))
    }
}


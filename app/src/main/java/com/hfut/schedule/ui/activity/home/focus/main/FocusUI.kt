package com.hfut.schedule.ui.activity.home.focus.main

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
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
import androidx.compose.ui.platform.LocalContext
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.ui.activity.home.calendar.communtiy.getCourseINFO
import com.hfut.schedule.ui.activity.home.cube.items.subitems.FocusCard
import com.hfut.schedule.ui.activity.home.focus.funictions.AddButton
import com.hfut.schedule.ui.activity.home.focus.funictions.AddItem
import com.hfut.schedule.ui.activity.home.focus.funictions.AddedItems
import com.hfut.schedule.ui.activity.home.focus.funictions.MySchedule
import com.hfut.schedule.ui.activity.home.focus.funictions.MyScheuleItem
import com.hfut.schedule.ui.activity.home.focus.funictions.MyWangKe
import com.hfut.schedule.ui.activity.home.focus.funictions.TimeStampItem
import com.hfut.schedule.ui.activity.home.focus.funictions.TodayCourseItem
import com.hfut.schedule.ui.activity.home.focus.funictions.TomorrowCourseItem
import com.hfut.schedule.ui.activity.home.focus.funictions.WangkeItem
//import com.hfut.schedule.ui.activity.home.focus.getResult
//import com.hfut.schedule.ui.activity.home.focus.newScheduleItems
//import com.hfut.schedule.ui.activity.home.focus.newWangkeItem
import com.hfut.schedule.ui.activity.home.main.saved.initNetworkRefresh
import com.hfut.schedule.ui.activity.home.search.functions.exam.JxglstuExamUI
import com.hfut.schedule.ui.activity.home.search.functions.exam.getExamJXGLSTU
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TodayScreen(vm : NetWorkViewModel, vm2 : LoginViewModel, innerPadding : PaddingValues, blur : Boolean, vmUI : UIViewModel, ifSaved : Boolean, webVpn : Boolean, state: PagerState,hazeState: HazeState) {

    val  TAB_LEFT = 0
    val TAB_RIGHT = 1

//刷新
    var refresh by remember { mutableStateOf(false) }
    var refreshing by remember { mutableStateOf(false) }
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    val states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
            }.await()
            async { initNetworkRefresh(vm,vm2,vmUI,ifSaved) }.await()
            launch {
                refreshing = false
            }
        }
    })
/////////////////////////////////////////逻辑函数区/////////////////////////////////////////////////
    LaunchedEffect(refresh) {
        if(!refresh) {
            async { initNetworkRefresh(vm,vm2,vmUI,ifSaved) }.await()
            launch { refresh = true }
        }
    }
//Today操作区///////////////////////////////////////////////////////////////////////////////////////////////////

    val activity = LocalActivity.current
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//布局///////////////////////////////////////


        Column(modifier = Modifier
            .fillMaxSize()
            ){

            Box(modifier = Modifier
                .fillMaxHeight()
                .pullRefresh(states)){
                val scrollstate = rememberLazyListState()
                val shouldShowAddButton by remember { derivedStateOf { scrollstate.firstVisibleItemScrollOffset == 0 } }
                var date = DateTimeUtils.Date_MM_dd
                val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
                var week = DateTimeUtils.weeksBetween.toInt()
              //  val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPI", apiCheck())
                var weekdaytomorrow = DateTimeUtils.dayweek + 1
                var weekdayToday = DateTimeUtils.dayweek
                var Nextweek = DateTimeUtils.weeksBetween.toInt()
                //当今天为周日时，变0为7
                //当第二天为下一周的周一时，周数+1
                when(weekdaytomorrow) { 1 -> Nextweek += 1 }
                when (weekdayToday) { 0 -> weekdayToday = 7 }
                //判断是否上完今天的课
                val todayCourseList = getCourseINFO(weekdayToday,week)
                val lastCourse = todayCourseList.lastOrNull()
                val lastTime = if(lastCourse != null) {
                    lastCourse[0].classTime.substringAfter("-")
                } else {
                    "00:00"
                }
                //lastTime字符串一定得到HH:MM格式，封装一个函数获取本地时间，再写代码比较两者

                HorizontalPager(state = state) { page ->
                    Scaffold {
                        LazyColumn(state = scrollstate) {
                            item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                            when(page) {
                                TAB_LEFT -> {
                                    item { FocusCard(vmUI,vm,refreshing,hazeState) }
                                    //课表
                                    if (DateTimeUtils.compareTimes(lastTime) != DateTimeUtils.TimeState.NOT_STARTED)
                                        items(getCourseINFO(weekdaytomorrow,Nextweek).size) { item -> TomorrowCourseItem(item = item,vm,hazeState) }
                                    else
                                        items(todayCourseList.size) { item -> TodayCourseItem(item = item,vm, hazeState) }
                                    //日程
                                    items(MySchedule().size) { item -> activity?.let { MyScheuleItem(item = item, MySchedule = MySchedule(),false,it) } }
                                    //考试
                                    items(getExamJXGLSTU()) { item -> JxglstuExamUI(item,false) }
                                    //网课
                                    items(MyWangKe().size) { item -> activity?.let { WangkeItem(item = item, MyWangKe = MyWangKe(),false,it) } }
                                }
                                TAB_RIGHT -> {
                                    //日程
                                    items(MySchedule().size) { item -> activity?.let { MyScheuleItem(item = item, MySchedule = MySchedule(),true,it) }  }
                                    //网课
                                    items(MyWangKe().size) { item -> activity?.let { WangkeItem(item = item, MyWangKe = MyWangKe(),true,it) } }
                                    //第二天课表
                                    if (DateTimeUtils.compareTimes(lastTime) == DateTimeUtils.TimeState.NOT_STARTED)
                                        items(getCourseINFO(weekdaytomorrow,Nextweek).size) { item -> TomorrowCourseItem(item = item,vm,hazeState) }

                                    items(AddedItems().size){ item -> AddItem(item = item, AddedItems = AddedItems()) }

                                    item { TimeStampItem() }
                                }
                            }
                            item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
                        }
                    }
                }
                AddButton(isVisible = shouldShowAddButton,innerPadding,hazeState)
                PullRefreshIndicator(refreshing, states, Modifier.padding(innerPadding).align(Alignment.TopCenter))
            }

        }
}



fun isTodayCoursesOvered() : Boolean {
    var date = DateTimeUtils.Date_MM_dd
    val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
    var week = DateTimeUtils.weeksBetween.toInt()
    //  val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPI", apiCheck())
    var weekdaytomorrow = DateTimeUtils.dayweek + 1
    var weekdayToday = DateTimeUtils.dayweek
    var Nextweek = DateTimeUtils.weeksBetween.toInt()
    //当今天为周日时，变0为7
    //当第二天为下一周的周一时，周数+1
    when(weekdaytomorrow) { 1 -> Nextweek += 1 }
    when (weekdayToday) { 0 -> weekdayToday = 7 }

    val todayCourseList = getCourseINFO(weekdayToday,week)
    val tomorrowCourseList = getCourseINFO(weekdaytomorrow,Nextweek)

    val lastCourse = todayCourseList.lastOrNull()
    val lastTime = if(lastCourse != null) {
        lastCourse[0].classTime.substringAfter("-")
    } else {
        "00:00"
    }

    val isTodayCoursesOvered = DateTimeUtils.compareTimes(lastTime) != DateTimeUtils.TimeState.NOT_STARTED
    val todayCoursesNum = todayCourseList.size
    val tomorrowCoursesNum = tomorrowCourseList.size
    //val isNeedGetUp =
    return isTodayCoursesOvered
}

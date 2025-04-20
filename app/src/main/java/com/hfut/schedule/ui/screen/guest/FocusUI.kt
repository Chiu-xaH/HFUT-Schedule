package com.hfut.schedule.ui.screen.guest

//import com.hfut.schedule.ui.activity.home.focus.getResult
//import com.hfut.schedule.ui.activity.home.focus.newScheduleItems
//import com.hfut.schedule.ui.activity.home.focus.newWangkeItem
import android.annotation.SuppressLint
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
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.sys.JxglstuCourseSchedule
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getCustomNetCourse
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getCustomSchedule
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getNetCourse
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getSchedule
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.screen.home.calendar.communtiy.getCourseINFO
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.focus.funiction.CustomItem
import com.hfut.schedule.ui.screen.home.focus.funiction.NetCourseItem
import com.hfut.schedule.ui.screen.home.focus.funiction.ScheduleItem
import com.hfut.schedule.ui.screen.home.focus.funiction.TimeStampItem
import com.hfut.schedule.ui.screen.home.focus.funiction.getTodayJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.getTomorrowJxglstuCourse
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.screen.home.initNetworkRefresh
import com.hfut.schedule.ui.component.RefreshIndicator
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TodayScreenNoLogin(vm : NetWorkViewModel, vm2 : LoginViewModel, innerPadding : PaddingValues, vmUI : UIViewModel, ifSaved : Boolean, webVpn : Boolean, state: PagerState, hazeState : HazeState) {

    val  TAB_LEFT = 0
    val TAB_RIGHT = 1
    var refreshing by remember { mutableStateOf(false) }
    var timeNow by remember { mutableStateOf(DateTimeUtils.Time_HH_MM) }

    var refreshDB by remember { mutableStateOf(false) }
    val showStorageFocus by DataStoreManager.showFocusFlow.collectAsState(initial = true)


//Today操作区///////////////////////////////////////////////////////////////////////////////////////////////////
    var customNetCourseList by remember { mutableStateOf<List<CustomEventDTO>>(emptyList()) }
    var customScheduleList by remember { mutableStateOf<List<CustomEventDTO>>(emptyList()) }
    val scheduleList = getSchedule()
    val netCourseList = getNetCourse()
    //刷新
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    val states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true

            }.await()
            async { DateTimeUtils.updateTime { timeNow = it } }.await()
            async { initGuestNetwork(vm,vm2) }.await()
            launch {
                refreshing = false
            }
        }
    })
    val isAddUIExpanded by remember { derivedStateOf { vmUI.isAddUIExpanded } }


    LaunchedEffect(refreshDB,isAddUIExpanded) {
        if(!isAddUIExpanded) {
            launch { customNetCourseList = getCustomNetCourse() }
            launch { customScheduleList = getCustomSchedule() }
        }
    }

    // 初始化
    LaunchedEffect(Unit) {
        // 冷启动
        launch {
            initGuestNetwork(vm,vm2)
        }
        // 加载数据库
        launch {
            launch { customNetCourseList = getCustomNetCourse() }
            launch { customScheduleList = getCustomSchedule() }
        }
    }

    val activity = LocalActivity.current


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//布局///////////////////////////////////////
    val scrollState = rememberLazyListState()

    Column(modifier = Modifier
        .fillMaxSize()
    ){

        Box(modifier = Modifier
            .fillMaxHeight()
            .pullRefresh(states)){
            HorizontalPager(state = state) { page ->
                Scaffold {
                    LazyColumn(state = scrollState) {
                        item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                        when(page) {
                            TAB_LEFT -> {
                                if(showStorageFocus)
                                    customScheduleList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = false) { refreshDB = !refreshDB } } } }

                                scheduleList.let { list -> items(list.size) { item -> activity?.let { ScheduleItem(list[item],false,it) } } }

                                if(showStorageFocus)
                                    customNetCourseList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1,isFuture = false) { refreshDB = !refreshDB } } } }

                                netCourseList.let { list -> items(list.size) { item -> activity?.let { NetCourseItem(list[item],false,it) } } }
                            }
                            TAB_RIGHT -> {
                                //日程
                                if(showStorageFocus)
                                    customScheduleList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = true) { refreshDB = !refreshDB } } } }

                                scheduleList.let { list -> items(list.size) { item -> activity?.let { ScheduleItem(list[item],true,it) }  } }
                                //网课
                                if(showStorageFocus)
                                    customNetCourseList.let { list -> items(list.size){ item -> activity?.let { it1 -> CustomItem(item = list[item], hazeState = hazeState, activity = it1, isFuture = true) { refreshDB = !refreshDB } } } }

                                netCourseList.let { list -> items(list.size) { item -> activity?.let { NetCourseItem(list[item],true,it) } } }

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
}


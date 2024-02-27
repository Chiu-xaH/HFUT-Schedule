package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.Focus.FocusCourse
import com.hfut.schedule.logic.datamodel.Jxglstu.datumResponse
import com.hfut.schedule.logic.utils.GetDate.formattedTime
import com.hfut.schedule.ui.ComposeUI.Focus.AddButton
import com.hfut.schedule.ui.ComposeUI.Focus.AddItem
import com.hfut.schedule.ui.ComposeUI.Focus.AddedItems
import com.hfut.schedule.ui.ComposeUI.Search.NotificationsCenter.NotificationItems
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.ComposeUI.Search.NotificationsCenter.getNotifications
import com.hfut.schedule.ui.ComposeUI.Focus.FutureMyScheuleItem
import com.hfut.schedule.ui.ComposeUI.Focus.MySchedule
import com.hfut.schedule.ui.ComposeUI.Focus.MyScheuleItem
import com.hfut.schedule.ui.ComposeUI.Focus.MyWangKe
import com.hfut.schedule.ui.ComposeUI.Focus.TimeStampItem
import com.hfut.schedule.ui.ComposeUI.Focus.TodayCardItem
import com.hfut.schedule.ui.ComposeUI.Focus.TodayCourseItem
import com.hfut.schedule.ui.ComposeUI.Focus.TomorrowCourseItem
import com.hfut.schedule.ui.ComposeUI.Focus.WangkeItem
import com.hfut.schedule.ui.ComposeUI.Focus.zjgdcard
import com.hfut.schedule.ui.ComposeUI.Saved.NetWorkUpdate
import com.hfut.schedule.ui.ComposeUI.SavedCourse.getCourseINFO
import com.hfut.schedule.ui.ComposeUI.Search.Exam.ExamItems
import com.hfut.schedule.ui.ComposeUI.Search.Exam.getExam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TodayScreen(vm : LoginSuccessViewModel,vm2 : LoginViewModel,innerPaddings : PaddingValues,blur : Boolean) {

    val  TAB_LEFT = 0
    val TAB_RIGHT = 1

/////////////////////////////////////////逻辑函数区/////////////////////////////////////////////////

//Today操作区///////////////////////////////////////////////////////////////////////////////////////////////////

    CoroutineScope(Job()).launch{
        async { NetWorkUpdate(vm,vm2) }
    }

    //刷新
    var refreshing by remember { mutableStateOf(false) }
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    var states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
                NetWorkUpdate(vm,vm2)
            }.await()
            async {
                refreshing = false
                MyToast("刷新成功")
            }
        }
    })

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//布局///////////////////////////////////////


        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddings)){
            var state by remember { mutableStateOf(TAB_LEFT) }
            val titles = listOf("重要安排","其他事项")
            Column {
                TabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = { Text(text = title) },
                            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur).5f else 1f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier
                .fillMaxHeight()
                .pullRefresh(states)){
                val scrollstate = rememberLazyListState()
                val shouldShowAddButton = scrollstate.firstVisibleItemScrollOffset  == 0
                var date = GetDate.Date_MM_dd
                val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
                var week = GetDate.Benweeks.toInt()

                var weekdaytomorrow = GetDate.dayweek + 1
                var weekdayToday = GetDate.dayweek
                var Nextweek = GetDate.Benweeks.toInt()
                //当今天为周日时，变0为7
                //当第二天为下一周的周一时，周数+1
                when(weekdaytomorrow) {1 -> Nextweek += 1 }
                when (weekdayToday) {0 -> weekdayToday = 7 }

                LazyColumn(state = scrollstate) {

                    //当Tab为第一个时
                    if (state == TAB_LEFT) {
                        //一卡通
                        if (prefs.getBoolean("SWITCHCARD",true) == true) {
                            zjgdcard(vm)
                            item { TodayCardItem(vm) }
                        }
                        //课表
                        if (GetDate.formattedTime.toInt() >= 18)
                            items(getCourseINFO(weekdaytomorrow,Nextweek).size) { item -> TomorrowCourseItem(item = item) }
                        else
                            items(getCourseINFO(weekdayToday,week).size) { item -> TodayCourseItem(item = item) }
                        //日程
                        if (prefs.getBoolean("SWITCHMYAPI",true)){
                            items(MySchedule().size) { item -> MyScheuleItem(item = item, MySchedule = MySchedule()) }
                        }
                        //考试
                        items(getExam().size) { item -> ExamItems(item,true) }
                     //   item{ AddItem(item = 0, AddedItems = )}
                    }

                    //当Tab为第二个时
                    if (state == TAB_RIGHT) {

                        if (prefs.getBoolean("SWITCHMYAPI",true)) {
                            //日程
                            items(MySchedule().size) { item -> FutureMyScheuleItem(item = item, MySchedule = MySchedule()) }
                            //网课
                            items(MyWangKe().size) { item -> WangkeItem(item = item, MyWangKe = MyWangKe()) }
                        }

                        //第二天课表
                        if (GetDate.formattedTime.toInt() < 18)
                            items(getCourseINFO(weekdaytomorrow,Nextweek).size) { item -> TomorrowCourseItem(item = item) }

                        items(AddedItems().size){ item -> AddItem(item = item, AddedItems = AddedItems()) }
                        item { TimeStampItem() }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
                if(prefs.getBoolean("SWITCHADD",true))
                AddButton(isVisible = shouldShowAddButton)
                PullRefreshIndicator(refreshing, states, Modifier.align(Alignment.TopCenter))
            }

        }
}

package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.Focus.FocusCourse
import com.hfut.schedule.logic.datamodel.Jxglstu.datumResponse
import com.hfut.schedule.ui.ComposeUI.Focus.AddButton
import com.hfut.schedule.ui.ComposeUI.Focus.AddItem
import com.hfut.schedule.ui.ComposeUI.Focus.AddedItems
import com.hfut.schedule.ui.ComposeUI.Search.NotificationsCenter.NotificationItems
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.ComposeUI.Search.NotificationsCenter.getNotifications
import com.hfut.schedule.ui.ComposeUI.Focus.ExamGet
import com.hfut.schedule.ui.ComposeUI.Focus.FutureMyScheuleItem
import com.hfut.schedule.ui.ComposeUI.Focus.MySchedule
import com.hfut.schedule.ui.ComposeUI.Focus.MyScheuleItem
import com.hfut.schedule.ui.ComposeUI.Focus.MyWangKe
import com.hfut.schedule.ui.ComposeUI.Focus.TodayCardItem
import com.hfut.schedule.ui.ComposeUI.Focus.TodayCourseItem
import com.hfut.schedule.ui.ComposeUI.Focus.TomorrowCourseItem
import com.hfut.schedule.ui.ComposeUI.Focus.WangkeItem
import com.hfut.schedule.ui.ComposeUI.Focus.zjgdcard
import com.hfut.schedule.ui.ComposeUI.Saved.getCourseINFO
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
fun TodayScreen(vm : LoginSuccessViewModel) {

    val dayweek = GetDate.dayweek
    var chinesenumber  = GetDate.chinesenumber

    val  TAB_LEFT = 0
    val TAB_RIGHT = 1


    var table_1 by rememberSaveable { mutableStateOf("") }
    var table_2 by rememberSaveable { mutableStateOf("") }
    var table_3 by rememberSaveable { mutableStateOf("") }
    var table_4 by rememberSaveable { mutableStateOf("") }
    var room_1 by rememberSaveable { mutableStateOf("") }
    var room_2 by rememberSaveable { mutableStateOf("") }
    var room_3 by rememberSaveable { mutableStateOf("") }
    var room_4 by rememberSaveable { mutableStateOf("") }
    var time_1 by rememberSaveable { mutableStateOf("") }
    var time_2 by rememberSaveable { mutableStateOf("") }
    var time_3 by rememberSaveable { mutableStateOf("") }
    var time_4 by rememberSaveable { mutableStateOf("") }


    var tomorrow_table_1 by rememberSaveable { mutableStateOf("") }
    var tomorrow_table_2 by rememberSaveable { mutableStateOf("") }
    var tomorrow_table_3 by rememberSaveable { mutableStateOf("") }
    var tomorrow_table_4 by rememberSaveable { mutableStateOf("") }
    var tomorrow_room_1 by rememberSaveable { mutableStateOf("") }
    var tomorrow_room_2 by rememberSaveable { mutableStateOf("") }
    var tomorrow_room_3 by rememberSaveable { mutableStateOf("") }
    var tomorrow_room_4 by rememberSaveable { mutableStateOf("") }
    var tomorrow_time_1 by rememberSaveable { mutableStateOf("") }
    var tomorrow_time_2 by rememberSaveable { mutableStateOf("") }
    var tomorrow_time_3 by rememberSaveable { mutableStateOf("") }
    var tomorrow_time_4 by rememberSaveable { mutableStateOf("") }

    when (dayweek) {
        1 -> chinesenumber = "一"
        2 -> chinesenumber = "二"
        3 -> chinesenumber = "三"
        4 -> chinesenumber = "四"
        5 -> chinesenumber = "五"
        6 ->  chinesenumber = "六"
        0 ->  chinesenumber = "日"
    }

/////////////////////////////////////////逻辑函数区/////////////////////////////////////////////////

    fun Datum(): MutableList<FocusCourse> {

        var TodayCourse = mutableListOf<FocusCourse>()
        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val json = prefs.getString("json", MyApplication.NullDatum)
        // Log.d("测试",json!!)
        val datumResponse = Gson().fromJson(json, datumResponse::class.java)
        val scheduleList = datumResponse.result.scheduleList
        val lessonList = datumResponse.result.lessonList
        val scheduleGroupList = datumResponse.result.scheduleGroupList

        for (i in 0 until scheduleList.size) {
            var starttime = scheduleList[i].startTime.toString()
            starttime =
                starttime.substring(0, starttime.length - 2) + ":" + starttime.substring(
                    starttime.length - 2
                )

            var room = scheduleList[i].room.nameZh
            val person = scheduleList[i].personName
            var date = scheduleList[i].date
            var scheduleid = scheduleList[i].lessonId.toString()

            var periods = scheduleList[i].periods
            var lessonType = scheduleList[i].lessonType

            var endtime = scheduleList[i].endTime.toString()
            endtime = endtime.substring(0, endtime.length - 2) + ":" + endtime.substring(endtime.length - 2)
            room = room.replace("学堂", "")

            for (k in 0 until scheduleGroupList.size) {

                val id = scheduleGroupList[k].lessonId.toString()
                val std = scheduleGroupList[k].stdCount
                if (scheduleid == id) {
                    periods = std

                }
            }

            for (j in 0 until lessonList.size) {
                val lessonlist_id = lessonList[j].id
                val INFO = lessonList[j].suggestScheduleWeekInfo
                val name = lessonList[j].courseName
                val courseTypeName = lessonList[j].courseTypeName
                if (scheduleid == lessonlist_id) {
                    scheduleid = name
                    // endtime = INFO
                    lessonType = courseTypeName
                }

            }

            val time = starttime + "-" + endtime



            if (date == GetDate.Date_yyyy_MM_dd) {

                when(scheduleList[i].startTime) {
                    800 -> {
                        time_1 = time
                        room_1 = room
                        table_1 = scheduleid
                    }
                    1010 -> {
                        time_2 = time
                        room_2 = room
                        table_2 = scheduleid
                    }
                    1400 -> {
                        time_3 = time
                        room_3 = room
                        table_3 = scheduleid
                    }
                    1600 -> {
                        time_4 = time
                        room_4 = room
                        table_4 = scheduleid
                    }
                }
            }

        }
        //去除空数组，重新组成顺序课表
        val Course = arrayOf(
            FocusCourse(time_1,table_1,room_1),
            FocusCourse(time_2,table_2,room_2),
            FocusCourse(time_3,table_3,room_3),
            FocusCourse(time_4,table_4,room_4)
        )

        //遍历数组，如果存在非空集合，则添加到新的数组中
        Course.forEach { if (it.time.contains("-") ) TodayCourse.add(it) }

        return TodayCourse
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun DatumTomorrow(): MutableList<FocusCourse> {

        val today = LocalDate.now() // 获取当前日期
        val tomorrow = today.plusDays(1) // 获取下一天的日期

        var TomorrowCourse = mutableListOf<FocusCourse>()

        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val json = prefs.getString("json", MyApplication.NullDatum)
        // Log.d("测试",json!!)
        val datumResponse = Gson().fromJson(json, datumResponse::class.java)
        val scheduleList = datumResponse.result.scheduleList
        val lessonList = datumResponse.result.lessonList
        val scheduleGroupList = datumResponse.result.scheduleGroupList

        for (i in 0 until scheduleList.size) {
            var starttime = scheduleList[i].startTime.toString()
            starttime =
                starttime.substring(0, starttime.length - 2) + ":" + starttime.substring(
                    starttime.length - 2
                )

            var room = scheduleList[i].room.nameZh

            var date = scheduleList[i].date
            var scheduleid = scheduleList[i].lessonId.toString()


            var endtime = scheduleList[i].endTime.toString()
            endtime = endtime.substring(0, endtime.length - 2) + ":" + endtime.substring(endtime.length - 2)
            room = room.replace("学堂", "")



            for (j in 0 until lessonList.size) {
                val lessonlist_id = lessonList[j].id
                val INFO = lessonList[j].suggestScheduleWeekInfo
                val name = lessonList[j].courseName
                val courseTypeName = lessonList[j].courseTypeName
                if (scheduleid == lessonlist_id) {
                    scheduleid = name
                    // endtime = INFO
                }

            }

            val time = starttime + "-" + endtime

            if (date == tomorrow.toString()) {

                when(scheduleList[i].startTime) {
                    800 -> {
                        tomorrow_time_1 = time
                        tomorrow_room_1 = room
                        tomorrow_table_1 = scheduleid
                    }
                    1010 -> {
                        tomorrow_time_2 = time
                        tomorrow_room_2 = room
                        tomorrow_table_2 = scheduleid
                    }
                    1400 -> {
                        tomorrow_time_3 = time
                        tomorrow_room_3 = room
                        tomorrow_table_3 = scheduleid
                    }
                    1600 -> {
                        tomorrow_time_4 = time
                        tomorrow_room_4 = room
                        tomorrow_table_4 = scheduleid
                    }
                }
            }
        }

        val Course = arrayOf(
            FocusCourse(tomorrow_time_1,tomorrow_table_1,tomorrow_room_1),
            FocusCourse(tomorrow_time_2,tomorrow_table_2,tomorrow_room_2),
            FocusCourse(tomorrow_time_3,tomorrow_table_3,tomorrow_room_3),
            FocusCourse(tomorrow_time_4,tomorrow_table_4,tomorrow_room_4)
        )

        Course.forEach { if (it.time.contains("-") ) TomorrowCourse.add(it) }
        return TomorrowCourse
    }

//Today操作区///////////////////////////////////////////////////////////////////////////////////////////////////
suspend fun FocusUpdate(){
    val CommuityTOKEN = prefs.getString("TOKEN","")
        CoroutineScope(Job()).apply {
            async {
                val json = prefs.getString("json", "")
                if (json?.contains("result") == true) {
                    async { Datum() }
                    async { DatumTomorrow() }
                } else MyToast("本地数据为空,请登录以更新数据")
            }
            async { ExamGet() }
            async { MyWangKe() }
            async { MySchedule() }
            async { AddedItems() }
            async { getNotifications() }
            async { CommuityTOKEN?.let { vm.Exam(it) } }
            async { CommuityTOKEN?.let { vm.GetCourse(it) } }
            async { zjgdcard(vm) }.await()
        }
    }

    CoroutineScope(Job()).launch{ FocusUpdate() }

    //刷新
    var refreshing by remember { mutableStateOf(false) }
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    var states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
                FocusUpdate()
            }.await()
            async {
                refreshing = false
                MyToast("刷新成功")
            }

        }
    })

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//布局///////////////////////////////////////

//通知中心
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        SharePrefs.Save("Notifications", getNotifications().size.toString())
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("消息中心") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    NotificationItems()
                }
            }
        }
    }


    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH")
    val formattedTime = currentTime.format(formatter)

    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {

                        TextButton(onClick = { showBottomSheet = true }) {
                            BadgedBox(badge = {
                                if (getNotifications().size.toString() != prefs.getString("Notifications",""))
                                Badge()
                                Log.d("s",
                                    getNotifications().size.toString() + prefs.getString("Notifications",""))
                            }) {
                                Icon(
                                    painterResource(id = R.drawable.notifications),
                                    contentDescription = ""
                                )
                            }
                        }

                },
                title = { Text("今天  第${GetDate.Benweeks}周  周${chinesenumber}  ${GetDate.Date_MM_dd}") }
            )
        },
    ) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()){
            var state by remember { mutableStateOf(TAB_LEFT) }
            val titles = listOf("重要安排","其他事项")
            Column {
                TabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = { Text(text = title) },
                            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
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
                var week2 = GetDate.Benweeks.toInt()
                //当第二天为周日时，变值为0
                //当第二天为下一周的周一时，周数+1
                when(weekdaytomorrow) {
                    7 -> weekdaytomorrow = 0
                    1 -> week2 += 1
                }

                LazyColumn(state = scrollstate) {

                    //当Tab为第一个时
                    if (state == TAB_LEFT) {

                        //一卡通
                        if (prefs.getBoolean("SWITCHCARD",true) == true) {
                            zjgdcard(vm)
                            item { TodayCardItem(vm) }
                        }
                        //课表
                        if (formattedTime.toInt() >= 18)
                            items(getCourseINFO(weekdaytomorrow,week2).size) { item -> TomorrowCourseItem(item = item) }
                        else
                            items(getCourseINFO(GetDate.dayweek,week).size) { item -> TodayCourseItem(item = item) }
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
                        if (formattedTime.toInt() < 18)
                            items(getCourseINFO(weekdaytomorrow,week2).size) { item -> TomorrowCourseItem(item = item) }

                        items(AddedItems().size){item -> AddItem(item = item, AddedItems = AddedItems()) }

                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
                if(prefs.getBoolean("SWITCHADD",true))
                AddButton(isVisible = shouldShowAddButton)
                PullRefreshIndicator(refreshing, states, Modifier.align(Alignment.TopCenter))
            }

        }
    }
}

package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.SharePrefs
import com.hfut.schedule.logic.datamodel.FocusCourse
import com.hfut.schedule.logic.datamodel.Jxglstu.datumResponse
import com.hfut.schedule.logic.datamodel.MyList
import com.hfut.schedule.logic.datamodel.Schedule
import com.hfut.schedule.logic.datamodel.zjgd.BalanceResponse
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.datamodel.Notifications
import com.hfut.schedule.ui.ComposeUI.Search.NotificationItems
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard.SchoolCardItem
import com.hfut.schedule.ui.ComposeUI.MyToast
import com.hfut.schedule.ui.ComposeUI.Search.getNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(vm : LoginSuccessViewModel) {


    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)

    val dayweek = GetDate.dayweek


    var chinesenumber  = GetDate.chinesenumber


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

//使用指尖工大接口获取一卡通余额
fun zjgdcard() {
    CoroutineScope(Job()).apply {
        launch {
            async {
                val auth = prefs.getString("auth","")
                vm.getyue("bearer " + auth)
            }.await()
            async {
                delay(500)
                val yue = prefs.getString("cardyue",MyApplication.NullCardblance)
                val yuedata = Gson().fromJson(yue,BalanceResponse::class.java)
                var num = yuedata.data.card[0].db_balance.toString()
                //待圈存
                var num_settle = yuedata.data.card[0].unsettle_amount.toString()


                var num_float = num.toFloat()
                var num_settle_float = num_settle.toFloat()
                num_float = num_float / 100
                SharePrefs.Save("card_now", num_float.toString())
                num_settle_float = num_settle_float / 100
                SharePrefs.Save("card_settle", num_settle_float.toString())
                num_float = num_settle_float + num_float

                SharePrefs.Save("card", num_float.toString())
            }
        }
    }
}

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

    fun ExamGet() : List<Map<String,String>>{
        //考试JSON解析

        val examjson = prefs.getString("exam", MyApplication.NullExam)

        val doc = Jsoup.parse(examjson).select("tbody tr")

        val data = doc.map { row ->
            val elements = row.select("td")
            val courseName = elements[0].text()
            val examRoom = elements[2].text()
            var  examtime = elements[1].text()

            examtime = examtime.replace("2023-","")
            examtime = examtime.replace("2024-","")

            mapOf("课程名称" to courseName,
                "日期时间" to examtime,
                "考场" to examRoom)
        }
        return data
    }





    fun MySchedule() : MutableList<Schedule> {

        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val my = prefs.getString("my",MyApplication.NullMy)
        val data = Gson().fromJson(my, MyAPIResponse::class.java).Lessons
        val list = data.Schedule
        var Schedule = mutableListOf<Schedule>()
        list.forEach { Schedule.add(it) }
        return Schedule
    }

    fun MyWangKe() : MutableList<MyList> {

        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val my = prefs.getString("my",MyApplication.NullMy)
        val data = Gson().fromJson(my, MyAPIResponse::class.java).Lessons
        val list = data.MyList
        var Wabgke = mutableListOf<MyList>()
        list.forEach {  Wabgke.add(it) }
        return Wabgke
    }

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

    var card =prefs.getString("card","正在获取")

    val token = prefs.getString("bearer","")




    //Today操作区///////////////////////////////////////////////////////////////////////////////////////////////////
    fun TodayUpdate() {
        CoroutineScope(Job()).apply {
            async { zjgdcard() }
            async {
                val json = prefs.getString("json", "")
                if (json?.contains("result") == true) {
                    Datum()
                    DatumTomorrow()
                } else MyToast("本地数据为空,请登录以更新数据")
            }
            async{ ExamGet() }
            async{ MyWangKe() }
            async{ MySchedule() }
            async { getNotifications() }
        }
    }


    TodayUpdate()
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                                Log.d("s",getNotifications().size.toString() + prefs.getString("Notifications",""))
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
            var state by remember { mutableStateOf(0) }
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


            var expand by remember { mutableStateOf(false) }
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {

    fun Tomorrow() {
     items(DatumTomorrow().size) { item ->
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                Spacer(modifier = Modifier.height(100.dp))
             Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    shape = MaterialTheme.shapes.medium,


                    ) {
                    ListItem(
                        headlineContent = {  Text(text = DatumTomorrow()[item].name) },
                        overlineContent = {Text(text = DatumTomorrow()[item].time)},
                        supportingContent = { Text(text = DatumTomorrow()[item].room)},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.exposure_plus_1),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {}
                    )
                }
            }
        }
    }

                fun Today() {

                    items(Datum().size) { item ->
                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
                        {
                            Spacer(modifier = Modifier.height(100.dp))
                            Card(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 3.dp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 5.dp),
                                shape = MaterialTheme.shapes.medium

                            ) {
                                ListItem(
                                    headlineContent = {  Text(text = Datum()[item].name) },
                                    overlineContent = {Text(text = Datum()[item].time)},
                                    supportingContent = { Text(text = Datum()[item].room)},
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.schedule),
                                            contentDescription = "Localized description",
                                        )
                                    }, modifier = Modifier.clickable {  }
                                )
                            }
                        }
                    }
                }

                if (state == 0) {

                    if (prefs.getBoolean("SWITCHCARD",true) == true) {
                        zjgdcard()
                        item {
                            Card(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 3.dp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 5.dp),
                                shape = MaterialTheme.shapes.medium

                            ){ SchoolCardItem(vm) }
                        }
                    }

                    items(ExamGet()) {item ->

                        var date = GetDate.Date_MM_dd
                        val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
                        val get = item["日期时间"]
                        val examdate = (get?.substring(0, 2) ) + get?.substring(3, 5)
                        //判断考完试不显示信息
                        if(examdate.toInt() >= todaydate.toInt()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Spacer(modifier = Modifier.height(100.dp))
                                Card(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 3.dp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp, vertical = 5.dp),
                                    shape = MaterialTheme.shapes.medium

                                )  {
                                    ListItem(
                                        headlineContent = {  Text(text = "${item["课程名称"]}") },
                                        overlineContent = {Text(text = "${item["日期时间"]}")},
                                        supportingContent = { Text(text = "${item["考场"]}")},
                                        leadingContent = { Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",) },
                                        modifier = Modifier.clickable {},
                                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                    )
                                }
                            }
                        }
                    }
                    if (prefs.getBoolean("SWITCHMYAPI",true)){
                        items(MySchedule().size) { item ->

                            var date = GetDate.Date_MM_dd
                            val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
                            val get = MySchedule()[item].time
                            val examdate = (get?.substring(0, 2) ) + get?.substring(3, 5)
                            //判断考完试不显示信息
                            if (examdate.toInt() == todaydate.toInt()) {
                                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
                                {
                                    Spacer(modifier = Modifier.height(100.dp))
                                    Card(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 3.dp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 5.dp)
                                        //.size(width = 350.dp, height = 90.dp)
                                        ,shape = MaterialTheme.shapes.medium

                                    ) {
                                        ListItem(
                                            headlineContent = {  Text(text = MySchedule()[item].title) },
                                            overlineContent = {Text(text = MySchedule()[item].time)},
                                            supportingContent = { Text(text = MySchedule()[item].info)},
                                            leadingContent = {
                                                if (MySchedule()[item].title.contains("实验"))
                                                    Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
                                                else
                                                    Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",)
                                            },
                                            modifier = Modifier.clickable {}
                                        )
                                    }
                                }
                            }
                        }
                    }



                    val currentTime = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofPattern("HH")
                    val formattedTime = currentTime.format(formatter)



                    if (formattedTime.toInt() >= 18) Tomorrow()
                    else Today()

                }

                if (state == 1) {
                    val currentTime = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofPattern("HH")
                    val formattedTime = currentTime.format(formatter)

                    if (prefs.getBoolean("SWITCHMYAPI",true)) {
                        items(MySchedule().size) { item ->

                            var date = GetDate.Date_MM_dd
                            val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
                            val get = MySchedule()[item].time
                            val examdate = (get?.substring(0, 2) ) + get?.substring(3, 5)
                            //判断考完试不显示信息
                            if (examdate.toInt() > todaydate.toInt()) {
                                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
                                {
                                    Spacer(modifier = Modifier.height(100.dp))
                                    Card(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 3.dp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 5.dp)
                                        //.size(width = 350.dp, height = 90.dp)
                                        ,shape = MaterialTheme.shapes.medium

                                    ) {
                                        ListItem(
                                            headlineContent = {  Text(text = MySchedule()[item].title) },
                                            overlineContent = {Text(text = MySchedule()[item].time)},
                                            supportingContent = { Text(text = MySchedule()[item].info)},
                                            leadingContent = {
                                                if (MySchedule()[item].title.contains("实验"))
                                                    Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
                                                else
                                                    Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",)
                                            },
                                            modifier = Modifier.clickable {}
                                        )
                                    }
                                }
                            }

                        }

                        items(MyWangKe().size) { item ->


                            var date = GetDate.Date_MM_dd
                            val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
                            val get = MyWangKe()[item].time
                            val Wangkedate = (get?.substring(0, 2) ) + get?.substring(3, 5)
                            //判断过期不显示信息
                            if(Wangkedate.toInt() >= todaydate.toInt()) {

                                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
                                {
                                    Spacer(modifier = Modifier.height(100.dp))
                                    Card(
                                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 5.dp),
                                        shape = MaterialTheme.shapes.medium,

                                        ) {

                                        ListItem(
                                            headlineContent = {  Text(text = MyWangKe()[item].title) },
                                            overlineContent = {Text(text = MyWangKe()[item].time)},
                                            supportingContent = { Text(text = MyWangKe()[item].info)},
                                            leadingContent = {
                                                Icon(
                                                    painterResource(R.drawable.net),
                                                    contentDescription = "Localized description",
                                                )
                                            },
                                            modifier = Modifier.clickable {}
                                        )
                                    }
                                }
                            }
                        }
                    }


                    if (formattedTime.toInt() < 18) { Tomorrow() }

                }

             item { Spacer(modifier = Modifier.height(100.dp)) }
            }

        }
    }
}
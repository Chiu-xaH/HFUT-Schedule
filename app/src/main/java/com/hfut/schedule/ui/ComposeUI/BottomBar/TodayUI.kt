package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.JxglstuViewModel
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.datamodel.course
import com.hfut.schedule.logic.datamodel.data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import java.time.LocalDate

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(vm : JxglstuViewModel) {


    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)

    val dayweek = GetDate.dayweek


    var chinesenumber  = GetDate.chinesenumber


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


    fun Datum(): MutableList<course> {

        var TodayCourse = mutableListOf<course>()
        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val json = prefs.getString("json", MyApplication.NullDatum)
        // Log.d("测试",json!!)
        val data = Gson().fromJson(json, data::class.java)
        val scheduleList = data.result.scheduleList
        val lessonList = data.result.lessonList
        val scheduleGroupList = data.result.scheduleGroupList

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



            if (date == GetDate.Date) {

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
            course(time_1,table_1,room_1),
            course(time_2,table_2,room_2),
            course(time_3,table_3,room_3),
            course(time_4,table_4,room_4)
        )

//遍历数组，如果存在非空集合，则添加到新的数组中
        Course.forEach { if (it.time.contains("-") ) TodayCourse.add(it) }

        return TodayCourse
    }




    fun DatumTomorrow(): MutableList<course> {

        var number2 : Int = 0

        val today = LocalDate.now() // 获取当前日期
        val tomorrow = today.plusDays(1) // 获取下一天的日期

        var TomorrowCourse = mutableListOf<course>()

        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val json = prefs.getString("json", MyApplication.NullDatum)
        // Log.d("测试",json!!)
        val data = Gson().fromJson(json, data::class.java)
        val scheduleList = data.result.scheduleList
        val lessonList = data.result.lessonList
        val scheduleGroupList = data.result.scheduleGroupList

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
            course(tomorrow_time_1,tomorrow_table_1,tomorrow_room_1),
            course(tomorrow_time_2,tomorrow_table_2,tomorrow_room_2),
            course(tomorrow_time_3,tomorrow_table_3,tomorrow_room_3),
            course(tomorrow_time_4,tomorrow_table_4,tomorrow_room_4)
        )


        Course.forEach { if (it.time.contains("-") ) TomorrowCourse.add(it) }

        return TomorrowCourse

    }

    val token = prefs.getString("bearer","")

        if (token  != null && token.contains("AT"))
            vm.getCard("Bearer $token")

    val card =prefs.getString("card","正在获取")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("今天  第${GetDate.Benweeks}周  周${chinesenumber}  ${GetDate.Date2}") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ){



            val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val json = prefs.getString("json", "")
            if (json?.contains("result") == true) {
                Datum()
                DatumTomorrow()
            } else Toast.makeText(MyApplication.context,"本地数据为空,请登录以更新数据",Toast.LENGTH_SHORT).show()


            ExamGet()

            var expand by remember { mutableStateOf(false) }
            Spacer(modifier = Modifier.height(10.dp))




          //  Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {
                item {
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium

                    ){
                        ListItem(
                            headlineContent = { Text(text = "一卡通余额   ${card} 元") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.credit_card),
                                    contentDescription = "Localized description",
                                )
                            },
                            trailingContent={
                                FilledTonalIconButton(onClick = {
                                                val it = Intent(Intent.ACTION_DEFAULT, Uri.parse(MyApplication.AlipayURL) )
                                                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                MyApplication.context.startActivity(it)
                                            }) {
                                                Icon( painterResource(R.drawable.add),
                                                    contentDescription = "Localized description",)
                                            }
                            },

                            modifier = Modifier.clickable {}
                        )
                    }
                }

                items(ExamGet()) {item ->

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
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.draw),
                                        contentDescription = "Localized description",
                                    )
                                },
                                modifier = Modifier.clickable {},
                                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            )

                        }
                    }
                }



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
                               .padding(horizontal = 15.dp, vertical = 5.dp)
                               //.size(width = 350.dp, height = 90.dp)
                               .clickable { expand = !expand },
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
                               }
                           )
                       }
                   }
               }

                items(DatumTomorrow().size) { item ->
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
             item {
                 Spacer(modifier = Modifier.height(100.dp))
             }
            }

        }
    }
}
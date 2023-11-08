package com.hfut.schedule.ui.ComposeUI

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.datamodel.data
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun NoNet() {

    var table_1_1 by rememberSaveable { mutableStateOf("") }
    var table_1_2 by rememberSaveable { mutableStateOf("") }
    var table_1_3 by rememberSaveable { mutableStateOf("") }
    var table_1_4 by rememberSaveable { mutableStateOf("") }
    var table_1_5 by rememberSaveable { mutableStateOf("") }
    var table_2_1 by rememberSaveable { mutableStateOf("") }
    var table_2_2 by rememberSaveable { mutableStateOf("") }
    var table_2_3 by rememberSaveable { mutableStateOf("") }
    var table_2_4 by rememberSaveable { mutableStateOf("") }
    var table_2_5 by rememberSaveable { mutableStateOf("") }
    var table_3_1 by rememberSaveable { mutableStateOf("") }
    var table_3_2 by rememberSaveable { mutableStateOf("") }
    var table_3_3 by rememberSaveable { mutableStateOf("") }
    var table_3_4 by rememberSaveable { mutableStateOf("") }
    var table_3_5 by rememberSaveable { mutableStateOf("") }
    var table_4_1 by rememberSaveable { mutableStateOf("") }
    var table_4_2 by rememberSaveable { mutableStateOf("") }
    var table_4_3 by rememberSaveable { mutableStateOf("") }
    var table_4_4 by rememberSaveable { mutableStateOf("") }
    var table_4_5 by rememberSaveable { mutableStateOf("") }



    val firstWeekStart = LocalDate.parse("2023-09-11")
    val today = LocalDate.now()
    val weeksBetween = ChronoUnit.WEEKS.between(firstWeekStart, today) + 1

    var Bianhuaweeks = weeksBetween  //切换周数
    val Benweeks = weeksBetween  //固定本周


    val Date2 = SimpleDateFormat("MM-dd").format(Date())

    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayweek = dayOfWeek - 1


    var chinesenumber  = ""

    when (dayweek) {
        1 -> chinesenumber = "一"
        2 -> chinesenumber = "二"
        3 -> chinesenumber = "三"
        4 -> chinesenumber = "四"
        5 -> chinesenumber = "五"
        6 ->  chinesenumber = "六"
        0 ->  chinesenumber = "日"
    }
    //填充UI与更新
    fun Update() {
        table_1_1 = ""
        table_1_2 = ""
        table_1_3 = ""
        table_1_4 = ""
        table_1_5 = ""
        table_2_1 = ""
        table_2_2 = ""
        table_2_3 = ""
        table_2_4 = ""
        table_2_5 = ""
        table_3_1 = ""
        table_3_2 = ""
        table_3_3 = ""
        table_3_4 = ""
        table_3_5 = ""
        table_4_1 = ""
        table_4_2 = ""
        table_4_3 = ""
        table_4_4 = ""
        table_4_5 = ""
        //////////////////////////////////////////////////////////////////////////////////
        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val json = prefs.getString("json", "")
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
            var endtime = scheduleList[i].endTime.toString()
            endtime = endtime.substring(
                0,
                endtime.length - 2
            ) + ":" + endtime.substring(endtime.length - 2)
            var room = scheduleList[i].room.nameZh
            val person = scheduleList[i].personName
            var id = scheduleList[i].lessonId.toString()

            room = room.replace("学堂","")

            for (j in 0 until lessonList.size) {
                val idj = lessonList[j].id
                val name = lessonList[j].courseName
                if (id == idj)
                    id = name
            }



            val text = starttime + "\n" + id + "\n" + room

            if (scheduleList[i].weekIndex == Bianhuaweeks.toInt()) {
                if (scheduleList[i].weekday == 1) {
                    if (scheduleList[i].startTime == 800) {
                        table_1_1 = text
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_1 = text
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_1 = text
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_1 = text
                    }
                }
                if (scheduleList[i].weekday == 2) {
                    if (scheduleList[i].startTime == 800) {
                        table_1_2 = text
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_2 = text
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_2 = text
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_2 = text
                    }
                }
                if (scheduleList[i].weekday == 3) {
                    if (scheduleList[i].startTime == 800) {
                        table_1_3 = text
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_3 = text
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_3 = text
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_3 = text
                    }
                }
                if (scheduleList[i].weekday == 4) {
                    if (scheduleList[i].startTime == 800) {
                        table_1_4 = text
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_4 = text
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_4 = text
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_4 = text
                    }
                }
                if (scheduleList[i].weekday == 5) {
                    if (scheduleList[i].startTime == 800) {
                        table_1_5 = text
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_5 = text
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_5 = text
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_4_5 = text
                    }
                }


            }

        }

        //////////////////////////////////////////////////////////////////////////////////
    }




    Update()//填充UI与更新



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("第${Benweeks}周  星期${chinesenumber}  ${Date2}") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {


            //在这里插入课程表布局

            val table = arrayOf(
                arrayOf(table_1_1, table_1_2, table_1_3, table_1_4, table_1_5),
                arrayOf(table_2_1, table_2_2, table_2_3, table_2_4, table_2_5),
                arrayOf(table_3_1, table_3_2, table_3_3, table_3_4, table_3_5),
                arrayOf(table_4_1, table_4_2, table_4_3, table_4_4, table_4_5)
            )

            Column{
                val interactionSource = remember { MutableInteractionSource() }
                val interactionSource2 = remember { MutableInteractionSource() }
                val interactionSource3 = remember { MutableInteractionSource() } // 创建一个
                val isPressed by interactionSource.collectIsPressedAsState()
                val isPressed2 by interactionSource2.collectIsPressedAsState()
                val isPressed3 by interactionSource3.collectIsPressedAsState()

                val scale = animateFloatAsState(
                    targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy) // 使用弹簧动画
                )

                val scale2 = animateFloatAsState(
                    targetValue = if (isPressed2) 0.9f else 1f, // 按下时为0.9，松开时为1
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy) // 使用弹簧动画
                )

                val scale3 = animateFloatAsState(
                    targetValue = if (isPressed3) 0.9f else 1f, // 按下时为0.9，松开时为1
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy) // 使用弹簧动画
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                        .height(520.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        5.dp,
                        Alignment.CenterHorizontally
                    )

                ) {
                    items(5) { columnIndex ->
                        val weekdays = columnIndex + 1
                        val chinese = arrayOf("一", "二", "三", "四", "五")
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "    周${chinese[columnIndex]} ",
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp
                            )

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(2.dp)
                            ) {
                                items(4) { rowIndex ->
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Card(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 3.dp
                                        ),
                                        modifier = Modifier
                                            .size(width = 63.dp, height = 100.dp),
                                        shape = MaterialTheme.shapes.extraSmall,
                                        onClick = {
                                            //
                                        }
                                    ) {
                                        Text(
                                            text = table[rowIndex][columnIndex],
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center

                                        )
                                    }

                                }
                            }

                        }
                    }
                }

                //按钮
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),horizontalArrangement = Arrangement.Center) {

                    FilledTonalButton(
                        onClick = {
                            if (Bianhuaweeks > 1) {
                                Bianhuaweeks-- - 1}
                            Update()
                        },modifier = Modifier.scale(scale.value),
                        interactionSource = interactionSource
                    ) {
                        Text(text = "上一周")
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    FilledTonalButton(
                        onClick = {
                            Bianhuaweeks = Benweeks
                            Update()
                        },modifier = Modifier.scale(scale2.value),
                        interactionSource = interactionSource2
                    ) {
                        Text(
                            text = "第${Bianhuaweeks}周",

                            )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    //显示第几周

                    FilledTonalButton(
                        onClick = {
                            if (Bianhuaweeks < 20) {
                                Bianhuaweeks++ + 1}
                            Update()
                        },modifier = Modifier.scale(scale3.value),
                        interactionSource = interactionSource3
                    ) {
                        Text(text = "下一周")
                    }
                }
            }



        }
    }



}
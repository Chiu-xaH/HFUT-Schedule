package com.hfut.schedule.ui.ComposeUI.NoNet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.GetDate.Benweeks
import com.hfut.schedule.logic.GetDate.Date2
import com.hfut.schedule.logic.GetDate.weeksBetween
import com.hfut.schedule.logic.datamodel.data

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@Composable
fun NoNet() {

    var Mon by rememberSaveable { mutableStateOf("") }
    var Tue by rememberSaveable { mutableStateOf("") }
    var Wed by rememberSaveable { mutableStateOf("") }
    var Thur by rememberSaveable { mutableStateOf("") }
    var Fri by rememberSaveable { mutableStateOf("") }

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

    var sheet_1_1 by rememberSaveable { mutableStateOf("") }
    var sheet_1_2 by rememberSaveable { mutableStateOf("") }
    var sheet_1_3 by rememberSaveable { mutableStateOf("") }
    var sheet_1_4 by rememberSaveable { mutableStateOf("") }
    var sheet_1_5 by rememberSaveable { mutableStateOf("") }
    var sheet_2_1 by rememberSaveable { mutableStateOf("") }
    var sheet_2_2 by rememberSaveable { mutableStateOf("") }
    var sheet_2_3 by rememberSaveable { mutableStateOf("") }
    var sheet_2_4 by rememberSaveable { mutableStateOf("") }
    var sheet_2_5 by rememberSaveable { mutableStateOf("") }
    var sheet_3_1 by rememberSaveable { mutableStateOf("") }
    var sheet_3_2 by rememberSaveable { mutableStateOf("") }
    var sheet_3_3 by rememberSaveable { mutableStateOf("") }
    var sheet_3_4 by rememberSaveable { mutableStateOf("") }
    var sheet_3_5 by rememberSaveable { mutableStateOf("") }
    var sheet_4_1 by rememberSaveable { mutableStateOf("") }
    var sheet_4_2 by rememberSaveable { mutableStateOf("") }
    var sheet_4_3 by rememberSaveable { mutableStateOf("") }
    var sheet_4_4 by rememberSaveable { mutableStateOf("") }
    var sheet_4_5 by rememberSaveable { mutableStateOf("") }





    var Bianhuaweeks = weeksBetween  //切换周数




    val dayweek = GetDate.dayweek


    var chinesenumber  = GetDate.chinesenumber

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
           // var endtime = scheduleList[i].endTime.toString()
         //   endtime = endtime.substring(
          //      0,
           //     endtime.length - 2
          //  ) + ":" + endtime.substring(endtime.length - 2)
            var room = scheduleList[i].room.nameZh
            val person = scheduleList[i].personName
            var scheduleid = scheduleList[i].lessonId.toString()
            var endtime = scheduleList[i].endTime.toString()
            var periods = scheduleList[i].periods
            var lessonType = scheduleList[i].lessonType
            var date = scheduleList[i].date

            room = room.replace("学堂","")

            date = date.replace("2023-","")
            date = date.replace("2024-","")



            for (k in 0 until scheduleGroupList.size) {

                val id = scheduleGroupList[k].lessonId.toString()
                val std = scheduleGroupList[k].stdCount
                if ( scheduleid == id) {
                    periods = std

                }
            }

            for (j in 0 until lessonList.size) {
                val lessonlist_id = lessonList[j].id
                val INFO = lessonList[j].suggestScheduleWeekInfo
                val courseTypeName = lessonList[j].courseTypeName
                val name = lessonList[j].courseName
                if (scheduleid == lessonlist_id) {
                    scheduleid = name
                    endtime = INFO
                    lessonType = courseTypeName
                }

            }





            val text = starttime + "\n" + scheduleid + "\n" + room
            val info =
                        //"课程:${id}"+ "\n"+
                        //"时间:${starttime}-${endtime}"+ "\n" +
                      //  "教室:${room}" + "\n"+
                        "教师:${person}"+ "  "+
                        "周数:${endtime}"+ "  "+
                        "人数:${periods}"+ "  "+
                        "类型:${lessonType}"

           // Log.d("信息",info)

                        if (scheduleList[i].weekIndex == Bianhuaweeks.toInt()) {
                if (scheduleList[i].weekday == 1) {
                    Mon = date
                    if (scheduleList[i].startTime == 800) {
                        table_1_1 = text
                        sheet_1_1 = info
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_1 = text
                        sheet_2_1 = info
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_1 = text
                        sheet_3_1 = info
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_1 = text
                        sheet_4_1 = info
                    }
                }
                if (scheduleList[i].weekday == 2) {
                    Tue = date
                    if (scheduleList[i].startTime == 800) {
                        table_1_2 = text
                        sheet_1_2 = info
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_2 = text
                        sheet_2_2 = info
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_2 = text
                        sheet_3_2 = info
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_2 = text
                        sheet_4_2 = info
                    }
                }
                if (scheduleList[i].weekday == 3) {
                    Wed = date
                    if (scheduleList[i].startTime == 800) {
                        table_1_3 = text
                        sheet_1_3 = info
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_3 = text
                        sheet_2_3 = info
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_3 = text
                        sheet_3_3 = info
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_3 = text
                        sheet_4_3 = info
                    }
                }
                if (scheduleList[i].weekday == 4) {
                    Thur = date
                    if (scheduleList[i].startTime == 800) {
                        table_1_4 = text
                        sheet_1_4 = info
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_4 = text
                        sheet_2_4 = info
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_4 = text
                        sheet_3_4 = info
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_4 = text
                        sheet_4_4 = info
                    }
                }
                if (scheduleList[i].weekday == 5) {
                    Fri = date
                    if (scheduleList[i].startTime == 800) {
                        table_1_5 = text
                        sheet_1_5 = info
                    }
                    if (scheduleList[i].startTime == 1010) {
                        table_2_5 = text
                        sheet_2_5 = info
                    }
                    if (scheduleList[i].startTime == 1400) {
                        table_3_5 = text
                        sheet_3_5 = info
                    }
                    if (scheduleList[i].startTime == 1600) {
                        table_4_5 = text
                        sheet_4_5 = info
                    }
                }


            }

        }

    }

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val json = prefs.getString("json", "")
if (json?.contains("result") == true) {
        Update()//填充UI与更新
} else Toast.makeText(MyApplication.context,"本地数据为空,请登录以更新数据",Toast.LENGTH_SHORT).show()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("今天  第${Benweeks}周  周${chinesenumber}  ${Date2}") }
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

            val sheet = arrayOf(
                arrayOf(sheet_1_1, sheet_1_2, sheet_1_3, sheet_1_4, sheet_1_5),
                arrayOf(sheet_2_1, sheet_2_2, sheet_2_3, sheet_2_4, sheet_2_5),
                arrayOf(sheet_3_1, sheet_3_2, sheet_3_3, sheet_3_4, sheet_3_5),
                arrayOf(sheet_4_1, sheet_4_2, sheet_4_3, sheet_4_4, sheet_4_5)
            )

            val week = arrayOf(Mon,Tue,Wed,Thur,Fri)



            Column{




                val interactionSource = remember { MutableInteractionSource() }
                val interactionSource2 = remember { MutableInteractionSource() }
                val interactionSource3 = remember { MutableInteractionSource() } // 创建一个
                val isPressed by interactionSource.collectIsPressedAsState()
                val isPressed2 by interactionSource2.collectIsPressedAsState()
                val isPressed3 by interactionSource3.collectIsPressedAsState()

                val scale = animateFloatAsState(
                    targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "" // 使用弹簧动画
                )

                val scale2 = animateFloatAsState(
                    targetValue = if (isPressed2) 0.9f else 1f, // 按下时为0.9，松开时为1
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "" // 使用弹簧动画
                )

                val scale3 = animateFloatAsState(
                    targetValue = if (isPressed3) 0.9f else 1f, // 按下时为0.9，松开时为1
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "" // 使用弹簧动画
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
                                text = "    周${chinese[columnIndex]}",
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                            )

                            Spacer(modifier = Modifier.height(1.dp))

                            Text(
                                text = "     ${week[columnIndex]}",
                                textAlign = TextAlign.Center,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )



                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(2.dp)
                            ) {
                                items(4) { rowIndex ->
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Card(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 3.dp
                                        ),
                                     //   colors = CardDefaults.cardColors(
                                      //      containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                      //  ),
                                        modifier = Modifier
                                            //.padding(horizontal = 0.dp, vertical = 0.dp)
                                            .size(width = 63.dp, height = 100.dp),

                                           // .clickable {},
                                        shape = MaterialTheme.shapes.extraSmall,
                                        onClick = {
                                          //  Log.d("测试",sheet[rowIndex][columnIndex])
                                            if (sheet[rowIndex][columnIndex].contains("课")) Toast.makeText(MyApplication.context, sheet[rowIndex][columnIndex], Toast.LENGTH_SHORT).show()
                                            else Toast.makeText(MyApplication.context,"空数据", Toast.LENGTH_SHORT).show()

                                       }
                                    ) {
                                        Text(
                                            text = table[rowIndex][columnIndex],
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center,
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
                        interactionSource = interactionSource,

                    ) {
                        Text(text = "上一周"
                            ///,color = MaterialTheme.colorScheme.primary
                    )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    FilledTonalButton(
                        onClick = {
                            Bianhuaweeks = Benweeks
                            Update()
                        },modifier = Modifier.scale(scale2.value),
                        interactionSource = interactionSource2,

                    ) {
                        Text(
                            text = "第${Bianhuaweeks}周",
                           // color = MaterialTheme.colorScheme.primary
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
                        interactionSource = interactionSource3,

                    ) {
                        Text(text = "下一周"
                            //,color = MaterialTheme.colorScheme.primary
                    )
                    }
                }
            }



        }
    }



}
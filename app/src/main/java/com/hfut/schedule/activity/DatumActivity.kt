package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.data
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date


class DatumActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datum)

        val date_Mon : TextView = findViewById(R.id.Date_Mon)
        val date_Tue : TextView = findViewById(R.id.Date_Tue)
        val date_Wed : TextView = findViewById(R.id.Date_Wed)
        val date_Thur : TextView = findViewById(R.id.Date_Thur)
        val date_Fri : TextView = findViewById(R.id.Date_Fri)

        val table_1_1 : Button = findViewById(R.id.Table_1_1)
        val table_1_2 : Button = findViewById(R.id.Table_1_2)
        val table_1_3 : Button = findViewById(R.id.Table_1_3)
        val table_1_4 : Button = findViewById(R.id.Table_1_4)
        val table_1_5 : Button = findViewById(R.id.Table_1_5)
        val table_2_1 : Button = findViewById(R.id.Table_2_1)
        val table_2_2 : Button = findViewById(R.id.Table_2_2)
        val table_2_3 : Button = findViewById(R.id.Table_2_3)
        val table_2_4 : Button = findViewById(R.id.Table_2_4)
        val table_2_5 : Button = findViewById(R.id.Table_2_5)
        val table_3_1 : Button = findViewById(R.id.Table_3_1)
        val table_3_2 : Button = findViewById(R.id.Table_3_2)
        val table_3_3 : Button = findViewById(R.id.Table_3_3)
        val table_3_4 : Button = findViewById(R.id.Table_3_4)
        val table_3_5 : Button = findViewById(R.id.Table_3_5)
        val table_4_1 : Button = findViewById(R.id.Table_4_1)
        val table_4_2 : Button = findViewById(R.id.Table_4_2)
        val table_4_3 : Button = findViewById(R.id.Table_4_3)
        val table_4_4 : Button = findViewById(R.id.Table_4_4)
        val table_4_5 : Button = findViewById(R.id.Table_4_5)

        val helloTv : TextView = findViewById(R.id.helloTv)
        val leftButton : Button = findViewById(R.id.LeftButton)
        val rightButton : Button = findViewById(R.id.RightButton)
        val centerTv : TextView = findViewById(R.id.CenterTV)

        val firstWeekStart = LocalDate.parse("2023-09-11")
        val today = LocalDate.now()
        val weeksBetween = ChronoUnit.WEEKS.between(firstWeekStart, today) + 1

        var Bianhuaweeks = weeksBetween  //切换周数
        val Benweeks = weeksBetween  //固定本周


       // val Date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val Date2 = SimpleDateFormat("MM-dd").format(Date())
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayweek = dayOfWeek - 1


        //calendar.add(Calendar.DAY_OF_MONTH, 1)
        //val day = SimpleDateFormat("MM-dd").format(calendar.time)
        var chinesenumber  = ""

        when (dayweek) {
            1 -> { date_Mon.setText(Date2)
                   chinesenumber = "一"
            }
            2 -> { date_Tue.setText(Date2)
                   chinesenumber = "二"
            }
            3 -> { date_Wed.setText(Date2)
                   chinesenumber = "三"
            }
            4 -> { date_Thur.setText(Date2)
                   chinesenumber = "四"
            }
            5 -> { date_Fri.setText(Date2)
                   chinesenumber = "五"
            }
            6 -> { chinesenumber = "六" }
            0 -> { chinesenumber = "日" }
        }

        helloTv.setText("   你好  第${Benweeks}周  星期${chinesenumber}  ${Date2}")//显示日期周数
        centerTv.setText("  第 ${Bianhuaweeks} 周  ")//显示切换到的周数


        val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val json = prefs.getString("json", "")
        //Log.d("传送",json!!)


        val data = Gson().fromJson(json, data::class.java)
        val scheduleList = data.result.scheduleList
        val lessonList = data.result.lessonList
        val scheduleGroupList = data.result.scheduleGroupList




        for (i in 0 until scheduleList.size) {

            var starttime = scheduleList[i].startTime.toString()
            starttime = starttime.substring(0,starttime.length - 2) + ":" + starttime.substring(starttime.length - 2)
            var endtime = scheduleList[i].endTime.toString()
            endtime = endtime.substring(0,endtime.length - 2) + ":" + endtime.substring(endtime.length - 2)
            val room = scheduleList[i].room.nameZh
            val person = scheduleList[i].personName
            var id = scheduleList[i].lessonId.toString()
          //  var std = scheduleList[i].periods

            for (j in 0 until lessonList.size) {
                val idj = lessonList[j].id
                val name = lessonList[j].courseName
                //val suggestScheduleWeekInfo = lessonList[i].suggestScheduleWeekInfo
                if (id == idj)
                    id = name

            }

            //for (k in 0 until scheduleGroupList.size) {
              //  val count = scheduleGroupList[k].stdCount
             //   val idk = scheduleGroupList[k].lessonId.toString()
               // if (id == idk)
                    //std = count
                    //未写下操作

          //  }
           // Log.d("测试",std.toString())

            val text = id +  "\n"+ room + "\n" +  starttime
            //+ "\n"  + endtime

            if (scheduleList[i].weekIndex == Bianhuaweeks.toInt()) {
                val table = arrayOf(
                    arrayOf(table_1_1, table_1_2, table_1_3, table_1_4, table_1_5),
                   arrayOf(table_2_1, table_2_2, table_2_3, table_2_4, table_2_5),
                    arrayOf(table_3_1, table_3_2, table_3_3, table_3_4, table_3_5),
                    arrayOf(table_4_1, table_4_2, table_4_3, table_4_4, table_4_5)
                )
                val startTimeMap = mapOf(800 to 0, 1010 to 1, 1400 to 2, 1600 to 3)

                for (weekday in 1..5) {

                    if (scheduleList[i].weekday == weekday) {

                        val index = startTimeMap.getOrDefault(scheduleList[i].startTime, -1)
                        if (index != -1) {
                            table[index][weekday - 1].text = text
                            table[index][weekday - 1].setOnClickListener {
                                AlertDialog.Builder(this).apply {
                                    setTitle(id)
                                    setMessage(
                                            "任课教师:${person}\n" +
                                            "时间:${starttime} - ${endtime}\n" +
                                            "地点:${room}\n"// +
                                            //"周数:${lessonList[i].suggestScheduleWeekInfo}"
                                    )
                                    setPositiveButton("好") { dialog, which ->
                                        //操作
                                    }
                                    show()
                                }

                            }
                         //   table[index][weekday - 1].background

                        }
                    }
                }
            }//填充界面



            centerTv.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val table = arrayOf(
                        arrayOf(table_1_1, table_1_2, table_1_3, table_1_4, table_1_5),
                        arrayOf(table_2_1, table_2_2, table_2_3, table_2_4, table_2_5),
                        arrayOf(table_3_1, table_3_2, table_3_3, table_3_4, table_3_5),
                        arrayOf(table_4_1, table_4_2, table_4_3, table_4_4, table_4_5)
                    )

                    for (i in 0 until table.size) {
                        for (j in 0 until table[i].size) {
                            table[i][j].setText("")
                        }
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    if (scheduleList[i].weekIndex == Bianhuaweeks.toInt()) {
                        val table = arrayOf(
                            arrayOf(table_1_1, table_1_2, table_1_3, table_1_4, table_1_5),
                            arrayOf(table_2_1, table_2_2, table_2_3, table_2_4, table_2_5),
                            arrayOf(table_3_1, table_3_2, table_3_3, table_3_4, table_3_5),
                            arrayOf(table_4_1, table_4_2, table_4_3, table_4_4, table_4_5)
                        )
                        val startTimeMap = mapOf(800 to 0, 1010 to 1, 1400 to 2, 1600 to 3)

                        for (weekday in 1..5) {

                            if (scheduleList[i].weekday == weekday) {
                                val index = startTimeMap.getOrDefault(scheduleList[i].startTime, -1)
                                if (index != -1) { table[index][weekday - 1].setText(text) }
                            }
                        }
                    }//填充界面
                    // Do something after the text has been changed
                }
            })


        }



        centerTv.setOnClickListener { //跳转回当前周
            Bianhuaweeks = Benweeks
            centerTv.setText("  第 ${Benweeks} 周  ")

             }
        rightButton.setOnClickListener { //切换下周
            if (Bianhuaweeks <= 19) { centerTv.setText("  第 ${Bianhuaweeks++ + 1} 周  ") }
            else Toast.makeText(this,"已经是第二十周",Toast.LENGTH_SHORT).show()
             }
        leftButton.setOnClickListener { //切换上周
            if (Bianhuaweeks > 1) { centerTv.setText("  第 ${Bianhuaweeks-- - 1} 周  ") }
            else Toast.makeText(this,"已经是第一周",Toast.LENGTH_SHORT).show()
        }

       // toolbar.setTitle("专业")
//////设置姓名

    }


}


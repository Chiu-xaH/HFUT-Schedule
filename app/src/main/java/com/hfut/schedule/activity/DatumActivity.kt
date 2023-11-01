package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button

import android.widget.TextView
import androidx.activity.ComponentActivity

import com.hfut.schedule.R
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.Date


class DatumActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datum)

        val date_Mon : TextView = findViewById(R.id.Date_Mon)
        val date_Tue : TextView = findViewById(R.id.Date_Tue)
        val date_Wed : TextView = findViewById(R.id.Date_Wed)
        val date_Thur : TextView = findViewById(R.id.Date_Thur)
        val date_Fri : TextView = findViewById(R.id.Date_Fri)

        val table_1_1 : TextView = findViewById(R.id.Table_1_1)
        val table_1_2 : TextView = findViewById(R.id.Table_1_2)
        val table_1_3 : TextView = findViewById(R.id.Table_1_3)
        val table_1_4 : TextView = findViewById(R.id.Table_1_4)
        val table_1_5 : TextView = findViewById(R.id.Table_1_5)
        val table_2_1 : TextView = findViewById(R.id.Table_2_1)
        val table_2_2 : TextView = findViewById(R.id.Table_2_2)
        val table_2_3 : TextView = findViewById(R.id.Table_2_3)
        val table_2_4 : TextView = findViewById(R.id.Table_2_4)
        val table_2_5 : TextView = findViewById(R.id.Table_2_5)
        val table_3_1 : TextView = findViewById(R.id.Table_3_1)
        val table_3_2 : TextView = findViewById(R.id.Table_3_2)
        val table_3_3 : TextView = findViewById(R.id.Table_3_3)
        val table_3_4 : TextView = findViewById(R.id.Table_3_4)
        val table_3_5 : TextView = findViewById(R.id.Table_3_5)
        val table_4_1 : TextView = findViewById(R.id.Table_4_1)
        val table_4_2 : TextView = findViewById(R.id.Table_4_2)
        val table_4_3 : TextView = findViewById(R.id.Table_4_3)
        val table_4_4 : TextView = findViewById(R.id.Table_4_4)
        val table_4_5 : TextView = findViewById(R.id.Table_4_5)

        val helloTv : TextView = findViewById(R.id.helloTv)
        val leftButton : Button = findViewById(R.id.LeftButton)
        val rightButton : Button = findViewById(R.id.RightButton)
        val centerTv : TextView = findViewById(R.id.CenterTV)



        //Thread.sleep(1000)
        //val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
       // val json = prefs.getString("datumjson", "")
        var week = 1

        val Date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        // 打印结果
        //*****伪代码Date与2023-09-11相差几周，则week++*****//

        helloTv.setText("   你好，本周第 ${week} 周，${Date}")//显示日期周数

        centerTv.setText("  第 ${week} 周  ")//显示切换到的周数

        centerTv.setOnClickListener { //跳转回当前周
             }
        rightButton.setOnClickListener { //切换下周
             }
        leftButton.setOnClickListener { //切换上周
             }



       //待开发
    }

    private fun show() {
        //界面填充
    }
    private fun refresh() {
        //刷新
    }
}


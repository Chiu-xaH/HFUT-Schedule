package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.result

class DatumActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.datum)
        val tv : TextView = findViewById(R.id.tv)

        Thread.sleep(3000)
        val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val json = prefs.getString("datumjson", "")
       // val result = Gson().fromJson(json,result::class.java)
        //val lessonList =result.lessonList
       // val scheduleGroupList = result.scheduleGroupList
        //val scheduleList = result.scheduleList
        Log.d("课程表",json!!)
        tv.text = json
       // Log.d("课程表",result.toString())
       // Log.d("课程表",lessonList.toString())
       // Log.d("课程表",scheduleList.toString())
       // Log.d("课程表",scheduleGroupList.toString())

       //待开发
    }
}


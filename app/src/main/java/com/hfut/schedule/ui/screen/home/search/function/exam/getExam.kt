package com.hfut.schedule.ui.screen.home.search.function.exam

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.community.ExamResponse
import com.hfut.schedule.logic.model.community.examArrangementList
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.storage.SharePrefs
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Locale

fun getExam() : List<examArrangementList> {
    val json = prefs.getString("Exam","")
    try {
        val result = Gson().fromJson(json,ExamResponse::class.java)
        val list = result.result.examArrangementList
        return list
    } catch (e:Exception) {
        return emptyList()
    }
}

@SuppressLint("SuspiciousIndentation")
fun getNewExam() : MutableList<examArrangementList> {
    val json = prefs.getString("Exam","")
    val AddExam = mutableListOf<examArrangementList>()
    try {
        val result = Gson().fromJson(json,ExamResponse::class.java)
        val list = result.result.examArrangementList
        val date = DateTimeUtils.Date_yyyy_MM_dd
        val todaydate = date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10)


        for (i in list.indices) {
            val name = list[i].courseName
            val place = list[i].place
            val st = list[i].formatStartTime
            val get = list[i].formatEndTime
            //判断考完试不显示信息
            val examdate = (get?.substring(0,4)+ get?.substring(5, 7) ) + get?.substring(8, 10)
            if(examdate.toInt() >= todaydate.toInt())
                AddExam.add(examArrangementList(name,place,st,get))
        }
        return AddExam
    } catch (e : Exception) {
        return AddExam
    }
}



fun getExamJXGLSTU() : List<Map<String,String>>{
    //考试JSON解析

    val examjson = SharePrefs.prefs.getString("examJXGLSTU", "")

    val doc = Jsoup.parse(examjson).select("tbody tr")
    try {
        val data = doc.map { row ->
            val elements = row.select("td")
            val courseName = elements[0].text()
            val examRoom = elements[2].text()
            val  examtime = elements[1].text()
            mapOf("课程名称" to courseName,
                "日期时间" to examtime,
                "考场" to examRoom)
        }

        //合理性检查
        /*data的每个元素 必须满足以下条件：
        [日期时间]的格式必须是 YYYY-MM-DD HH:MM~HH-MM
        */
        val filteredData = data.filter {
            isValidDateTime(it["日期时间"] ?: "")
        }
        return filteredData
    } catch (e:Exception) {
        e.printStackTrace()
        return emptyList()
    }
}
//[日期时间]的格式必须是 YYYY-MM-DD HH:MM~HH-MM
fun isValidDateTime(str : String) : Boolean {
    val regex = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}~\d{2}:\d{2}""")
    if (!regex.matches(str)) {
        return false
    }

    return try {
        val parts = str.split(" ")
        val datePart = parts[0]
        val timeRange = parts[1].split("~")
        val timeStart = timeRange[0]
        val timeEnd = timeRange[1]

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        // 验证日期和时间部分是否有效
        dateFormat.parse(datePart)
        timeFormat.parse(timeStart)
        timeFormat.parse(timeEnd)
        true
    } catch (e: Exception) {
        false
    }
}
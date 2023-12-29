package com.hfut.schedule.ui.ComposeUI.Search.Exam

import android.util.Log
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.Community.ExamResponse
import com.hfut.schedule.logic.datamodel.Community.courseFailRateDTOList
import com.hfut.schedule.logic.datamodel.Community.examArrangementList
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import org.jsoup.Jsoup

fun ExamGet() : List<Map<String,String>>{
    //考试JSON解析

    val examjson = SharePrefs.prefs.getString("exam", MyApplication.NullExam)

    val doc = Jsoup.parse(examjson).select("tbody tr")

    val data = doc.map { row ->
        val elements = row.select("td")
        val courseName = elements[0].text()
        val examRoom = elements[2].text()
        val  examtime = elements[1].text()
        mapOf("课程名称" to courseName,
            "日期时间" to examtime,
            "考场" to examRoom)
    }
    return data
}

fun getExam() : MutableList<examArrangementList> {
    val json = prefs.getString("Exam",MyApplication.NullExams)
    val result = Gson().fromJson(json,ExamResponse::class.java)
    val list = result.result.examArrangementList
    var AddExam = mutableListOf<examArrangementList>()
    for (i in 0 until list.size) {
        val name = list[i].courseName
        val place = list[i].place
        val starttime = list[i].formatStartTime
        val endtime = list[i].formatEndTime
        AddExam.add(examArrangementList(name,place,starttime,endtime))
    }
    return AddExam
}

fun getNewExam() : MutableList<examArrangementList> {
    val json = prefs.getString("Exam",MyApplication.NullExams)
    val result = Gson().fromJson(json,ExamResponse::class.java)
    val list = result.result.examArrangementList
    var date = GetDate.Date_yyyy_MM_dd
    val todaydate = date?.substring(0, 4) + date?.substring(5, 7)  + date?.substring(8, 10)

    var AddExam = mutableListOf<examArrangementList>()
    for (i in 0 until list.size) {
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
}
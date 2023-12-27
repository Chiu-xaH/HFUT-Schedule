package com.hfut.schedule.ui.ComposeUI.Search.Exam

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.SharePrefs
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
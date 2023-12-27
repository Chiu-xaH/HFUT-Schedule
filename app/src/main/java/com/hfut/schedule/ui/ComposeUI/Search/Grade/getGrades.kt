package com.hfut.schedule.ui.ComposeUI.Search.Grade

import com.hfut.schedule.logic.datamodel.GradeResponse
import com.hfut.schedule.logic.utils.SharePrefs
import org.jsoup.Jsoup

fun getGrade() :  MutableList<GradeResponse> {
    val html = SharePrefs.prefs.getString("grade", "")
    val doc = Jsoup.parse(html)
    val rows = doc.select("tr")
    val list = mutableListOf<GradeResponse>()
    // Log.d("html",rows.toString())
    for(row in rows) {

        val tds = row.select("td") // 选择tr标签下的所有td标签
        if(!tds.isEmpty()) {
            val titles = tds[0].text()
            val scores =tds[3].text()
            val gpas = tds[4].text()
            val grades = tds[6].text()
            val Grade = GradeResponse(titles,scores,gpas,grades)
            list.add(Grade)
        }
    }
    return list
}
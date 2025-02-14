package com.hfut.schedule.ui.activity.grade

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.community.GradeResponse
import com.hfut.schedule.logic.beans.community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.beans.community.scoreInfoDTOList
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.viewmodel.NetWorkViewModel
import org.jsoup.Jsoup

fun getGrade() :  List<scoreInfoDTOList> {
    val json = SharePrefs.prefs.getString("Grade", MyApplication.NullGrades)
    try {
        val result = Gson().fromJson(json,GradeResponse::class.java).result
        return result.scoreInfoDTOList
    } catch (e:Exception) {
        return emptyList()
    }

}

fun getGradeJXGLSTU(vm : NetWorkViewModel) :  MutableList<GradeResponseJXGLSTU> {
    try {
        val html = vm.jxglstuGradeData.value
        val doc = Jsoup.parse(html)
        val rows = doc.select("tr")
        val list = mutableListOf<GradeResponseJXGLSTU>()
        // Log.d("html",rows.toString())
        for(row in rows) {

            val tds = row.select("td") // 选择tr标签下的所有td标签
            if(!tds.isEmpty()) {
                val titles = tds[0].text()
                val scores =tds[3].text()
                val gpas = tds[4].text()
                val totalgrade = tds[5].text()
                val grades = tds[6].text()
                val Grade = GradeResponseJXGLSTU(titles,scores,gpas,grades,totalgrade)
                list.add(Grade)
            }
        }
        return list
    } catch (e:Exception) {
       return mutableListOf()
    }
}
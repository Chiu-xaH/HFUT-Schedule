package com.hfut.schedule.ui.activity.grade

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.community.GradeResponse
import com.hfut.schedule.logic.beans.community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.beans.community.scoreInfoDTOList
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.viewmodel.NetWorkViewModel
import org.jsoup.Jsoup

fun getGrade() :  MutableList<scoreInfoDTOList> {
    val json = SharePrefs.prefs.getString("Grade", MyApplication.NullGrades)
    val result = Gson().fromJson(json,GradeResponse::class.java).result
    val list = result?.scoreInfoDTOList
    var Addlist = mutableListOf<scoreInfoDTOList>()
    if (list != null) {
        for (i in 0 until list.size){
            val name = list[i].courseName
            val score = list[i].score
            val credit = list[i].credit
            val gpa = list[i].gpa
            val pass = list[i].pass
            Addlist.add(scoreInfoDTOList(name,score, credit, gpa, pass))
        }
    }
    return Addlist
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
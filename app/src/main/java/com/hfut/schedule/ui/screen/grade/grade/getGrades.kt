package com.hfut.schedule.ui.screen.grade.grade

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.community.GradeResponse
import com.hfut.schedule.logic.model.community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.model.community.scoreInfoDTOList
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import org.jsoup.Jsoup

fun getGrade(vm: NetWorkViewModel) :  List<scoreInfoDTOList> {
    try {
        val result = Gson().fromJson(vm.GradeData.value,GradeResponse::class.java).result
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
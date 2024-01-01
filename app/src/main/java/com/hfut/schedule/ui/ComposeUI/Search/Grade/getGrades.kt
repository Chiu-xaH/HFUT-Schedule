package com.hfut.schedule.ui.ComposeUI.Search.Grade

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.Community.scoreInfoDTOList
import com.hfut.schedule.logic.utils.SharePrefs

fun getGrade() :  MutableList<scoreInfoDTOList> {
    val json = SharePrefs.prefs.getString("Grade", MyApplication.NullGrades)
    val result = Gson().fromJson(json,com.hfut.schedule.logic.datamodel.Community.GradeResponse::class.java).result
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
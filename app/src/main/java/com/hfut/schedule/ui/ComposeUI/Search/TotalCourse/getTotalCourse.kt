package com.hfut.schedule.ui.ComposeUI.Search.TotalCourse

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.Community.CourseTotalResponse
import com.hfut.schedule.logic.datamodel.Community.courseBasicInfoDTOList
import com.hfut.schedule.logic.utils.SharePrefs

fun getCourse(): MutableList<courseBasicInfoDTOList>  {
    val json = SharePrefs.prefs.getString("Course",MyApplication.NullTotal)
    val result = Gson().fromJson(json, CourseTotalResponse::class.java).result
    val list = result.courseBasicInfoDTOList
    var Addlist = mutableListOf<courseBasicInfoDTOList>()
    for(i in 0 until list.size) {
        val credit = list[i].credit
        val name = list[i].courseName
        Addlist.add(courseBasicInfoDTOList(name,credit))
    }
    return Addlist
}
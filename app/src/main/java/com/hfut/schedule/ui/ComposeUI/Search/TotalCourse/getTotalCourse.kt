package com.hfut.schedule.ui.ComposeUI.Search.TotalCourse

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.Community.CourseTotalResponse
import com.hfut.schedule.logic.datamodel.Community.courseBasicInfoDTOList
import com.hfut.schedule.logic.datamodel.Community.courseDetailDTOList
import com.hfut.schedule.logic.utils.SharePrefs

fun getCourse(): MutableList<courseBasicInfoDTOList>  {
    val json = SharePrefs.prefs.getString("Course",MyApplication.NullTotal)
    val result = Gson().fromJson(json, CourseTotalResponse::class.java).result
    val list = result.courseBasicInfoDTOList
    var Addlist = mutableListOf<courseBasicInfoDTOList>()
    var AddDetaillist = mutableListOf<courseDetailDTOList>()
    for(i in 0 until list.size) {
        val credit = list[i].credit
        val name = list[i].courseName
        val classes = list[i].className
        val type = list[i].trainingCategoryName_dictText
        val detailList = list[i].courseDetailDTOList
        for(j in 0 until detailList.size) {
            val section = detailList[j].section
            val sectionCount = detailList[j].sectionCount
            val place = detailList[j].place
            val teacher = detailList[j].teacher
            val classTime = detailList[j].classTime
            val weekCount = detailList[j].weekCount
            val week = detailList[j].week
            AddDetaillist.add(courseDetailDTOList(section, sectionCount, place, teacher, classTime, weekCount, week,name))
        }
        Addlist.add(courseBasicInfoDTOList(name,credit,classes,type,AddDetaillist))
    }
    return Addlist
}


fun getDetailCourse(item : Int,name : String) : MutableList<courseDetailDTOList> {
    val json = SharePrefs.prefs.getString("Course",MyApplication.NullTotal)
    val result = Gson().fromJson(json, CourseTotalResponse::class.java).result
    val detailList = result.courseBasicInfoDTOList[item].courseDetailDTOList
    var AddDetaillist = mutableListOf<courseDetailDTOList>()

        for(j in 0 until detailList.size) {
            val section = detailList[j].section
            val sectionCount = detailList[j].sectionCount
            val place = detailList[j].place
            val teacher = detailList[j].teacher
            val classTime = detailList[j].classTime
            val weekCount = detailList[j].weekCount
            val week = detailList[j].week
            AddDetaillist.add(courseDetailDTOList(section, sectionCount, place, teacher, classTime, weekCount, week, name))
        }
    return AddDetaillist
}
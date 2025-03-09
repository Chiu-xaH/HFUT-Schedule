package com.hfut.schedule.ui.activity.home.search.functions.totalCourse

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.community.CourseResult
import com.hfut.schedule.logic.beans.community.CourseTotalResponse
import com.hfut.schedule.logic.beans.community.courseBasicInfoDTOList
import com.hfut.schedule.logic.beans.community.courseDetailDTOList
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.parse.SemseterParser
import com.hfut.schedule.logic.utils.parse.getMy
import java.time.LocalDate

fun getCourse(friendUserName : String? = null): List<courseBasicInfoDTOList>  {
    return try {
        getFormCommunity(friendUserName)!!.courseBasicInfoDTOList
    } catch (e:Exception) {
        emptyList()
    }
}


fun getDetailCourse(item : Int,friendUserName : String? = null) : List<courseDetailDTOList> {
    return try {
        getFormCommunity(friendUserName)!!.courseBasicInfoDTOList[item].courseDetailDTOList
    } catch (e:Exception) {
        emptyList()
    }
}

fun getFormCommunity(friendUserName : String? = null): CourseResult? {
    val json = SharePrefs.prefs.getString(if(friendUserName == null) "Course" else "Course${friendUserName}",null)
    return try {
        Gson().fromJson(json, CourseTotalResponse::class.java).result
    } catch (e:Exception) {
        null
    }
}


fun getStartWeek() : LocalDate {
    try {
        val start = getFormCommunity()!!.start.substringBefore(" ")
        return LocalDate.parse(start)
    } catch (e : Exception) {
        var start = getMy()?.startDay
        if(start == null) {
            val month = DateTimeUtils.Date_MM.toIntOrNull() ?: 9
            start = when(month) {
                1 -> DateTimeUtils.Date_yyyy + "-02-23"
                in 2..7 -> DateTimeUtils.Date_yyyy + "-02-23"
                in 8..12 -> DateTimeUtils.Date_yyyy + "-09-09"
                else -> DateTimeUtils.Date_yyyy + "-09-09"
            }
        }
        return LocalDate.parse(start)
    }
}
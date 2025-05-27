package com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.hfut.schedule.logic.model.community.CourseResult
import com.hfut.schedule.logic.model.community.CourseTotalResponse
import com.hfut.schedule.logic.model.community.courseBasicInfoDTOList
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getMy
import java.time.LocalDate
// 之前的奇葩脑回路，完全看不懂咋写的
private fun getCourse(friendUserName : String? = null): List<courseBasicInfoDTOList>  {
    return try {
        getFormCommunity(friendUserName)!!.courseBasicInfoDTOList
    } catch (e:Exception) {
        emptyList()
    }
}

private fun getCoursesFromCommunity(targetWeek : Int, friendUserName : String? = null) : List<List<MutableList<courseDetailDTOList>>> {
//    val dayArray = Array(7) { Array<List<courseDetailDTOList>>(12) { emptyList() } }
    val dayArray : List<List<MutableList<courseDetailDTOList>>> = List(7) { List(12) { mutableListOf<courseDetailDTOList>() } }
    val result = getCourse(friendUserName)
    for (i in result.indices){
        val name = result[i].courseName
        val list = result[i].courseDetailDTOList
        for(j in list.indices) {
            val section = list[j].section
            val weekCount = list[j].weekCount
            val week = list[j].week
            weekCount.forEach { item ->
                if(item == targetWeek) {
                    list[j].name = name
                    dayArray[week - 1][section - 1].add(list[j])
                }
            }
        }
    }
    return dayArray
}

// weekday 周几 week 第几周 friendUserName 好友课表学号 空为自己课表
fun getCourseInfoFromCommunity(weekday : Int, week : Int, friendUserName : String? = null) : List<List<courseDetailDTOList>> {
    val result = mutableListOf<List<courseDetailDTOList>>()
    return try {
        if(weekday <= 7) {
            val days = getCoursesFromCommunity(week,friendUserName)[weekday - 1]
            for (i in days.indices){
                if(days[i].isNotEmpty())
                    days[i].forEach { _ -> result.add(days[i]) }
            }
            result
        } else result
    } catch (e : Exception) {
        e.printStackTrace()
        result
    }
}



fun getFormCommunity(friendUserName : String? = null): CourseResult? {
    val json = prefs.getString(if(friendUserName == null) "Course" else "Course${friendUserName}",null)
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
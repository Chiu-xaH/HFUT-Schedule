package com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse

import com.google.gson.Gson
import com.hfut.schedule.logic.model.community.CourseResult
import com.hfut.schedule.logic.model.community.CourseTotalResponse
import com.hfut.schedule.logic.model.community.courseBasicInfoDTOList
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.network.util.MyApiParse.getMy
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.formatter_YYYY_MM_DD
import java.time.LocalDate

private fun parseDatumCourse(result: String) : List<lessons> = try {
    Gson().fromJson(result,lessonResponse::class.java).lessons
} catch (e : Exception) {
    emptyList<lessons>()
}


fun getJxglstuStartDate(): LocalDate {
    try {
        val list = parseDatumCourse(prefs.getString("courses","")!!)
        return LocalDate.parse(list[0].semester.startDate, formatter_YYYY_MM_DD)
    } catch (e : Exception) {
        return getStartWeekFromCommunity()
    }
}


// 之前的奇葩脑回路，完全看不懂咋写的
private fun getCourse(friendUserName : String? = null): List<courseBasicInfoDTOList>  {
    return try {
        getFormCommunity(friendUserName)!!.courseBasicInfoDTOList
    } catch (e:Exception) {
        emptyList()
    }
}

private fun getCoursesFromCommunity(targetWeek : Int, friendUserName : String? = null) : List<List<MutableList<courseDetailDTOList>>> {
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


fun getStartWeekFromCommunity() : LocalDate {
    try {
        val start = getFormCommunity()!!.start.substringBefore(" ")
        return LocalDate.parse(start)
    } catch (e : Exception) {
        var start = getMy()?.startDay
        if(start == null) {
            val month = DateTimeManager.Date_MM.toIntOrNull() ?: 9
            start = when(month) {
                1 -> DateTimeManager.Date_yyyy + "-02-23"
                in 2..7 -> DateTimeManager.Date_yyyy + "-02-23"
                in 8..12 -> DateTimeManager.Date_yyyy + "-09-08"
                else -> DateTimeManager.Date_yyyy + "-09-08"
            }
        }
        return LocalDate.parse(start)
    }
}
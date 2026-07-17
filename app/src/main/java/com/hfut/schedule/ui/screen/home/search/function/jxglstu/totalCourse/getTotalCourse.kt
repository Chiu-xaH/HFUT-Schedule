package com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse

import com.hfut.schedule.network.model.response.community.CommunityTotalCourse
import com.hfut.schedule.network.model.response.community.CommunityTotalCourseResponse
import com.hfut.schedule.network.model.response.community.CommunityCourseBasicInfo
import com.hfut.schedule.network.model.response.community.CommunityCourseDetail
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.network.MyApiParse.getMy
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.formatter_YYYY_MM_DD
import com.hfut.schedule.network.helper.GsonInstance
import com.xah.common.logic.util.LogUtil
import java.time.LocalDate

private fun parseDatumCourse(result: String) : List<lessons> = try {
    GsonInstance.fromJson(result,lessonResponse::class.java).lessons
} catch (e : Exception) {
    LogUtil.error(e)
    emptyList<lessons>()
}

fun getDefaultStartTerm() =  getMy()?.startDay ?: getStartWeekFromCommunity()



fun safelySetDate(
    termStartDate : String
): LocalDate {
    return try {
        LocalDate.parse(
            termStartDate, formatter_YYYY_MM_DD
        )

    } catch (e : Exception) {
        LogUtil.error(e)
        LocalDate.now()
    }
}

suspend fun updateStartDate(json : String) {
    try {
        val list = parseDatumCourse(json)
        DataStoreManager.saveTermStartDate(list[0].semester.startDate)
    } catch (e : Exception) {
        LogUtil.error(e)
        DataStoreManager.saveTermStartDate(getStartWeekFromCommunity())
    }
}



// 之前的奇葩脑回路，完全看不懂咋写的
private fun getCourse(friendUserName : String? = null): List<CommunityCourseBasicInfo>  {
    return try {
        getFormCommunity(friendUserName)!!.basicInfoList
    } catch (e:Exception) {
        LogUtil.error(e)
        emptyList()
    }
}

fun getCoursesFromCommunity(targetWeek : Int, friendUserName : String? = null) : List<List<MutableList<CommunityCourseDetail>>> {
    val dayArray : List<List<MutableList<CommunityCourseDetail>>> = List(7) { List(12) { mutableListOf<CommunityCourseDetail>() } }
    val result = getCourse(friendUserName)
    for (i in result.indices){
        val name = result[i].courseName
        val list = result[i].detailList
        for(j in list.indices) {
            val section = list[j].section
            val weekCount = list[j].weekCount
            val week = list[j].week
            weekCount.forEach { item ->
                if(item == targetWeek) {
                    dayArray[week - 1][section - 1].add(list[j].copy(name = name))
                }
            }
        }
    }
    return dayArray
}

// weekday 周几 week 第几周 friendUserName 好友课表学号 空为自己课表
fun getCourseInfoFromCommunity(weekday : Int, week : Int, friendUserName : String? = null) : List<List<CommunityCourseDetail>> {
    val result = mutableListOf<List<CommunityCourseDetail>>()
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
        LogUtil.error(e)
        result
    }
}



fun getFormCommunity(friendUserName : String? = null): CommunityTotalCourse? {
    val json = prefs.getString(if(friendUserName == null) "Course" else "Course${friendUserName}",null)
    return try {
        GsonInstance.fromJson(json, CommunityTotalCourseResponse::class.java).result
    } catch (e:Exception) {
        LogUtil.error(e)
        null
    }
}


private fun getStartWeekFromCommunity() : String {
    try {
        val start = getFormCommunity()!!.start.substringBefore(" ")
        return start
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
        LogUtil.error(e)
        return start
    }
}
package com.hfut.schedule.logic.model.zhijian

import com.hfut.schedule.network.model.response.zhijian.ZhiJianCourseTable
import com.hfut.schedule.ui.screen.home.calendar.common.parseSingleChineseDigit
import com.hfut.schedule.ui.screen.home.calendar.common.simplifyPlace
import com.xah.common.logic.util.LogUtil

data class ZhiJianCourseTableDto(
    val courseName : String,
    val startPeriod : Int,
    val endPeriod : Int,
    val place : String?,
    val teacher : String,
    val department : String,
    val classes : String,
    val date : String,
    val code : String,
    val type : String,
    val weekday : Int,
)


fun ZhiJianCourseTable.toDto() : ZhiJianCourseTableDto? =
    try {
        val start = startPeriod.toInt()
        val end = start + period.toInt() - 1
        ZhiJianCourseTableDto(
            courseName = courseName,
            startPeriod = start,
            endPeriod = end,
            place = if(place == "暂无数据") null else place?.substringAfter(",")?.simplifyPlace(),
            teacher = teacher,
            department = department.substringBefore("（"),
            classes = classes,
            date = date,
            code = code,
            type = type,
            weekday = parseSingleChineseDigit(weekday[1]),
        )
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }
package com.hfut.schedule.ui.screen.home.calendar.timetable.logic

import com.hfut.schedule.ui.screen.home.calendar.common.simplifyPlace

/**
 * @param startTime 传入HH-MM
 * @param endTime 传入HH-MM
 * @param dayOfWeek 周几 注意周日是7
 */
data class TimeTableItem(
    val type : TimeTableType,
    val name: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val place : String? = null,
    val teacher : String? = null,
    val detail : TimeTableDetail
) {
    fun getSimplyPlace() : String? = place?.simplifyPlace()
}

data class TimeTableDetail(
    // 给长按预览用的参数
    val teacher : String?,
    // 给长按预览标题用的参数
    val date : String,
    // 给空教室用的参数
    val classes : String? = null,
    // 给空教室用的参数
    val code : String? = null,
    // 给日程用的参数
    val eventId : Int? = null
)
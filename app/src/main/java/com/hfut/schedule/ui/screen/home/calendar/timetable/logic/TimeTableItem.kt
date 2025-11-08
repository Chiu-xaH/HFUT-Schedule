package com.hfut.schedule.ui.screen.home.calendar.timetable.logic

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
    val id : Int? = null,
)
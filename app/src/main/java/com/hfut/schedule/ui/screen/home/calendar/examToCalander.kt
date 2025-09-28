package com.hfut.schedule.ui.screen.home.calendar

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.getExamJXGLSTU
import java.time.LocalDate

data class ExamToCalenderBean(
    val day : String?,//YYYY-MM-DD
    val startTime: String?,//HH:MM
    val place: String?,
    val course: String?,
)

fun examToCalendar() : List<ExamToCalenderBean> {
    val examMaps: List<Map<String, String>> = getExamJXGLSTU()
    val newList = mutableListOf<ExamToCalenderBean>()

    return try {
        for (examMap in examMaps) {
            val day = examMap["日期时间"]?.substringBefore(" ")
                val startTime = examMap["日期时间"]?.substringAfter(" ")?.substringBefore("~")
                val place = examMap["考场"]
                val course = examMap["课程名称"]
                val examToCalendarBean = ExamToCalenderBean(day, startTime, place, course)
                newList.add(examToCalendarBean)
        }
        newList
    } catch (e:Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun getScheduleDate(showAll: Boolean, today : LocalDate) : List<String> {
    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)
    val dateList = mutableListOf<String>()
    for(i in 0..if(showAll)7 else 5) {
        val date = mondayOfCurrentWeek.plusDays(i.toLong()).toString()
        dateList.add(date)
    }
    return dateList
}
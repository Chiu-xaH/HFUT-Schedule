package com.hfut.schedule.ui.screen.home.calendar

import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.getExamJXGLSTU
import java.time.LocalDate

data class ExamToCalanderBean(
    val day : String?,//YYYY-MM-DD
    val startTime: String?,//HH:MM
    val place: String?,
    val course: String?,
)

fun examToCalendar() : List<ExamToCalanderBean> {
    val examMaps: List<Map<String, String>> = getExamJXGLSTU()
    //”MM-DD“ "STARTTIME" "PLACE" "TITLE"
    val newList = mutableListOf<ExamToCalanderBean>()

    return try {
        for (examMap in examMaps) {
            val day = examMap["日期时间"]?.substringBefore(" ")
            val dayLong = day?.replace("-","")?.toLong() ?: 0L
            val todayLong = DateTimeUtils.Date_yyyy_MM_dd.replace("-","").toLong()

//            if(todayLong <= dayLong) {
                val startTime = examMap["日期时间"]?.substringAfter(" ")?.substringBefore("~")
                val place = examMap["考场"]
                val course = examMap["课程名称"]
                val examToCalendarBean = ExamToCalanderBean(day, startTime, place, course)
                newList.add(examToCalendarBean)
//            }
        }
        newList
    } catch (e:Exception) {
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
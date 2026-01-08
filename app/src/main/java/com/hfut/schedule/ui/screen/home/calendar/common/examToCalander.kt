package com.hfut.schedule.ui.screen.home.calendar.common

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.getExamFromCache
import com.xah.uicommon.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class ExamToCalenderBean(
    val day : String?,//YYYY-MM-DD
    val startTime: String?,//HH:MM
    val endTime : String?,
    val place: String?,
    val course: String?,
    val type : String? = null
)

suspend fun examToCalendar() : List<ExamToCalenderBean> = withContext(Dispatchers.IO) {
    val examMaps= getExamFromCache()
    val newList = mutableListOf<ExamToCalenderBean>()

    return@withContext try {
        for (examMap in examMaps) {
            val day = examMap.dateTime.substringBefore(" ")
            val time = examMap.dateTime.substringAfter(" ").split("~")
            if(time.size != 2) {
                continue
            }
            val startTime = time[0]
            val endTime = time[1]
            val place = examMap.place
            val course = examMap.name
            val examToCalendarBean = ExamToCalenderBean(day, startTime,endTime, place, course,examMap.type)
            newList.add(examToCalendarBean)
        }
        newList
    } catch (e:Exception) {
        LogUtil.error(e)
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
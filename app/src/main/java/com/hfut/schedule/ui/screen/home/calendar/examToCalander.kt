package com.hfut.schedule.ui.screen.home.calendar

import android.content.Context
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.getExamFromCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class ExamToCalenderBean(
    val day : String?,//YYYY-MM-DD
    val startTime: String?,//HH:MM
    val place: String?,
    val course: String?,
)

suspend fun examToCalendar(context: Context) : List<ExamToCalenderBean> = withContext(Dispatchers.IO) {
    val examMaps= getExamFromCache(context)
    val newList = mutableListOf<ExamToCalenderBean>()

    return@withContext try {
        for (examMap in examMaps) {
            val day = examMap.dateTime.substringBefore(" ")
            val startTime = examMap.dateTime.substringAfter(" ").substringBefore("~")
            val place = examMap.place
            val course = examMap.name
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
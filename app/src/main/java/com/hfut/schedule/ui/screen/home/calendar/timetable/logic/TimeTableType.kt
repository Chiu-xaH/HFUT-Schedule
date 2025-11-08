package com.hfut.schedule.ui.screen.home.calendar.timetable.logic

import com.hfut.schedule.R

enum class TimeTableType(
    val description: String,
    val icon : Int
) {
    COURSE("课程", R.drawable.calendar),FOCUS("日程", R.drawable.lightbulb),EXAM("考试", R.drawable.draw)
}
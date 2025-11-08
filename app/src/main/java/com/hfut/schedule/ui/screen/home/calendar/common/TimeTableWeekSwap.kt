package com.hfut.schedule.ui.screen.home.calendar.common

interface TimeTableWeekSwap {
    fun nextWeek()
    fun previousWeek()
    fun goToWeek(i : Long)
    fun backToCurrentWeek()
}
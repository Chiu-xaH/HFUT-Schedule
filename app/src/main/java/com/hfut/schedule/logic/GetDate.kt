package com.hfut.schedule.logic

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

object GetDate {
    @RequiresApi(Build.VERSION_CODES.O)
    val firstWeekStart = LocalDate.parse("2023-09-11")
    @RequiresApi(Build.VERSION_CODES.O)
    val today = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val weeksBetween = ChronoUnit.WEEKS.between(firstWeekStart, today) + 1

    @RequiresApi(Build.VERSION_CODES.O)
    var Bianhuaweeks = weeksBetween  //切换周数
    @RequiresApi(Build.VERSION_CODES.O)
    val Benweeks = weeksBetween  //固定本周
    val Date3 = SimpleDateFormat("yyyy-MM").format(Date())

    val Date2 = SimpleDateFormat("MM-dd").format(Date())


    val Date = SimpleDateFormat("yyyy-MM-dd").format(Date())

    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayweek = dayOfWeek - 1





    var chinesenumber  = ""



}
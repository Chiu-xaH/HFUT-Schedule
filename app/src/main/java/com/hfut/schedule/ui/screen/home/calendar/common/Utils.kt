package com.hfut.schedule.ui.screen.home.calendar.common

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getJxglstuStartDate
import java.time.LocalDate
import java.time.temporal.ChronoUnit


fun numToChinese(num : Int) : String {
    return when(num) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "日"
        else -> ""
    }
}

// 传入YYYY-MM-DD 返回当前第几周周几
fun dateToWeek(date : String) : Pair<Int,Int>? {
    try {
        // 第一周的开始日期 为周一  LocalDate
        val start = getJxglstuStartDate()
        val target = LocalDate.parse(date)

        val days = ChronoUnit.DAYS.between(start, target)
        return if (days < 0) {
            // 目标日期早于学期开始
            null
        } else {
            val week = (days / 7 + 1).toInt()   // 第几周（从1开始）
            val dayOfWeek = ((days % 7) + 1).toInt() // 周几（1=周一，7=周日）
            Pair(week, dayOfWeek)
        }
    } catch (e : Exception) {
        e.printStackTrace()
        return null
    }
}
// 反函数
fun weekToDate(week : Int,weekday : Int) : String? {
    try {
        if (week < 1 || weekday !in 1..7) return null

        val start = getJxglstuStartDate()
        val daysToAdd = (week - 1) * 7L + (weekday - 1)
        val target = start.plusDays(daysToAdd)
        return target.toString() // 返回 "YYYY-MM-DD"
    } catch (e : Exception) {
        e.printStackTrace()
        return null
    }
}

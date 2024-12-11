package com.hfut.schedule.logic.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.Community.CourseTotalResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

object GetDate {
    /////////////周数 社区课表
    val json = prefs.getString("Course", MyApplication.NullTotal)


    val result = Gson().fromJson(json,CourseTotalResponse::class.java).result


    val start = result.start.substringBefore(" ")
    @RequiresApi(Build.VERSION_CODES.O)
    val firstWeekStart: LocalDate = LocalDate.parse(start)
    @RequiresApi(Build.VERSION_CODES.O)
    val today: LocalDate = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val weeksBetween = ChronoUnit.WEEKS.between(firstWeekStart, today) + 1

    @RequiresApi(Build.VERSION_CODES.O)
    val Benweeks = weeksBetween  //固定本周

    ///////////////////////////////////
    @SuppressLint("SimpleDateFormat")
    val Date_yyyy_MM = SimpleDateFormat("yyyy-MM").format(Date())
    @SuppressLint("SimpleDateFormat")
    val Date_MM_dd = SimpleDateFormat("MM-dd").format(Date())
    @SuppressLint("SimpleDateFormat")
    val Date_MM = SimpleDateFormat("MM").format(Date())
    @SuppressLint("SimpleDateFormat")
    val Date_dd = SimpleDateFormat("dd").format(Date())
    @SuppressLint("SimpleDateFormat")
    val Date_yyyy = SimpleDateFormat("yyyy").format(Date())
    @SuppressLint("SimpleDateFormat")
    val Date_yyyy_MM_dd = SimpleDateFormat("yyyy-MM-dd").format(Date())

    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayweek = dayOfWeek - 1

    var chinesenumber  = ""

    //获取时间
    @RequiresApi(Build.VERSION_CODES.O)
    val currentTime = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter_Hour = DateTimeFormatter.ofPattern("HH")
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter_Minute = DateTimeFormatter.ofPattern("MM")
    @RequiresApi(Build.VERSION_CODES.O)
    val formattedTime_Hour = currentTime.format(formatter_Hour)
    @RequiresApi(Build.VERSION_CODES.O)
    val formattedTime_Minute = currentTime.format(formatter_Minute)

    //解析
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    val tomorrow = today.plusDays(1).format(DateTimeFormatter.ofPattern("MM-dd"))
    /////////////周数 教务课表
    //计算目前已经过了多久
    @RequiresApi(Build.VERSION_CODES.O)
    fun getPercent(startDateStr: String, endDateStr: String): Double {
        // 将字符串转换为 LocalDate
        val startDate = LocalDate.parse(startDateStr, formatter)
        val endDate = LocalDate.parse(endDateStr, formatter)
        val currentDate = LocalDate.now()

        // 计算总天数和已过天数
        val totalDays = endDate.toEpochDay() - startDate.toEpochDay()
        val pastDays = currentDate.toEpochDay() - startDate.toEpochDay()

        // 计算百分比
        val percentage = (pastDays.toDouble() / totalDays.toDouble()) * 100
        return percentage
    }
}
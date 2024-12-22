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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

object GetDate {
    /////////////周数 社区课表
    private val json = prefs.getString("Course", MyApplication.NullTotal)


    private val result = Gson().fromJson(json,CourseTotalResponse::class.java).result


    private val start = result.start.substringBefore(" ")
    @RequiresApi(Build.VERSION_CODES.O)
    private val firstWeekStart: LocalDate = LocalDate.parse(start)
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

    private val calendar = Calendar.getInstance()
    private val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayweek = dayOfWeek - 1

    var chinesenumber  = ""

    //获取时间
    val currentTime = LocalDateTime.now()

    private val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
    val formatterTime_HH_MM = currentTime.format(formatterTime)
    val formattedTime_Hour = currentTime.format(DateTimeFormatter.ofPattern("HH"))
    val formattedTime_Minute = currentTime.format(DateTimeFormatter.ofPattern("MM"))

    //解析
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val tomorrow = today.plusDays(1).format(DateTimeFormatter.ofPattern("MM-dd"))
    /////////////周数 教务课表
    //计算目前已经过了多久
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

    fun compareTimes(lastTime: String): Int {
        // 将字符串转换为 LocalTime 格式

        val last = LocalTime.parse(lastTime, formatterTime)
        val now = LocalTime.parse(formatterTime_HH_MM, formatterTime)

        // 比较时间：返回值 <0 表示 last 早于 now，>0 表示 last 晚于 now，0 表示相等
        return last.compareTo(now)
    }

    /*
    传入startTime,endTime都是HH:MM字符串，比较本地时间，是否进行中，未开始，已结束，返回三种结果，用枚举enums定义返回类型
     */

    enum class TimeState {
        ONGOING,      // 进行中
        NOT_STARTED,  // 未开始
        ENDED         // 已结束
    }

    fun getTimeState(startTime: String, endTime: String): TimeState {
        val start = LocalTime.parse(startTime, formatterTime)
        val end = LocalTime.parse(endTime, formatterTime)
        val now = LocalTime.parse(formatterTime_HH_MM, formatterTime)

        return when {
            now.isBefore(start) -> TimeState.NOT_STARTED // 当前时间早于开始时间
            now.isAfter(end) -> TimeState.ENDED         // 当前时间晚于结束时间
            else -> TimeState.ONGOING                  // 当前时间在开始和结束之间
        }
    }

}
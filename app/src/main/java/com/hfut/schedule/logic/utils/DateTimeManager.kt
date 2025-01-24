package com.hfut.schedule.logic.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.community.CourseTotalResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

object DateTimeManager {
    /////////////周数 社区课表
    private val json = prefs.getString("Course", MyApplication.NullTotal)
    private val result = Gson().fromJson(json,CourseTotalResponse::class.java).result
    private val start = result.start.substringBefore(" ")
    private val firstWeekStart: LocalDate = LocalDate.parse(start)
    /////////////////////////////////////////////////////////
    val today: LocalDate = LocalDate.now()
    val weeksBetween = ChronoUnit.WEEKS.between(firstWeekStart, today) + 1
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
    //////////////////////////////////////////////////////////
    private val calendar = Calendar.getInstance()
    private val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayweek = dayOfWeek - 1 //周几
    var chinesenumber  = ""
    /////////////////////////////////////////////////////////
    //时间
    private val currentTime = LocalDateTime.now()

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
//        val currentDate = LocalDate.now()
        // 计算总天数和已过天数
        val totalDays = endDate.toEpochDay() - startDate.toEpochDay()
        val pastDays = today.toEpochDay() - startDate.toEpochDay()
        // 计算百分比
        val percentage = (pastDays.toDouble() / totalDays.toDouble()) * 100
        return percentage
    }

    //<0是已完成 >0未完成
    fun compareTimes(lastTime: String): TimeState {
        // 将字符串转换为 LocalTime 格式
        try {
            val last = LocalTime.parse(lastTime, formatterTime)
            val now = LocalTime.parse(formatterTime_HH_MM, formatterTime)

            // 比较时间：返回值 <0 表示 last 早于 now，>0 表示 last 晚于 now，0 表示相等
            return if(last > now) {
                TimeState.NOT_STARTED
            } else if(last < now) {
                TimeState.ENDED
            } else {
                TimeState.ONGOING
            }
        } catch (e: Exception) {
            return TimeState.NOT_STARTED
        }
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

    fun daysBetween(dateString: String): Long {
        val inputDate = LocalDate.parse(dateString, formatter)
        // 计算两个日期之间的天数
        return ChronoUnit.DAYS.between(today, inputDate)
    }

}
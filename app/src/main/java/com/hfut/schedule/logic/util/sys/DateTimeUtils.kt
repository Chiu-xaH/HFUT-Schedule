package com.hfut.schedule.logic.util.sys

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getStartWeek
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.MonthDay
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date

object DateTimeUtils {
    // 解析
    val simpleFormatter_YYYY_MM = SimpleDateFormat("yyyy-MM")
    val simpleFormatter_YYYY_MM_DD = SimpleDateFormat("yyyy-MM-dd")
    val simpleFormatter_MM_DD = SimpleDateFormat("MM-dd")
    val simpleFormatter_Year = SimpleDateFormat("yyyy")
    val simpleFormatter_Month = SimpleDateFormat("MM")
    val simpleFormatter_Day = SimpleDateFormat("dd")

    val formatter_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formatter_YYYY_MM = DateTimeFormatter.ofPattern("yyyy-MM")
    val formatter_MM_DD = DateTimeFormatter.ofPattern("MM-dd")

    val formatterTime_HH_MM = DateTimeFormatter.ofPattern("HH:mm")
    val formatterTime_HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss")
    val formatterTime_Hour = DateTimeFormatter.ofPattern("HH")
    val formatterTime_Minute = DateTimeFormatter.ofPattern("mm")
    val formatterTime_Second = DateTimeFormatter.ofPattern("ss")

    // 常量池
    const val APP_BIRTHDAY = "2023-10-16"

    // 日期
    private var today: LocalDate = LocalDate.now()
    private var currentTime = LocalDateTime.now()
    private var date = Date()

    val Date_yyyy_MM: String = simpleFormatter_YYYY_MM.format(date)
    val Date_MM_dd: String = simpleFormatter_MM_DD.format(date)
    val Date_MM: String = simpleFormatter_Month.format(date)
    val Date_dd: String = simpleFormatter_Day.format(date)
    val Date_yyyy: String = simpleFormatter_Year.format(date)
    val Date_yyyy_MM_dd: String = simpleFormatter_YYYY_MM_DD.format(date)

    // 周数 社区课表
    private val firstWeekStart: LocalDate = getStartWeek()
    val weeksBetween = ChronoUnit.WEEKS.between(firstWeekStart, today) + 1
    private var dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    //周几
    val dayWeek = dayOfWeek - 1

    //时间
    val Time_HH_MM = currentTime.format(formatterTime_HH_MM)
    val Time_HH_MM_SS = currentTime.format(formatterTime_HH_MM_SS)
    val Time_Hour = currentTime.format(formatterTime_Hour)
    val Time_Minute = currentTime.format(formatterTime_Minute)
    val Time_Second = currentTime.format(formatterTime_Second)

    // 明天
    val tomorrow_MM_DD: String = today.plusDays(1).format(formatter_MM_DD)
    val tomorrow_YYYY_MM_DD: String = today.plusDays(1).format(formatter_YYYY_MM_DD)

    val DateTime_T = Date_yyyy_MM_dd + "T" + Time_HH_MM_SS
    val DateTimeBeanNow = DateTimeBean(Date_yyyy.toInt(), Date_MM.toInt(), Date_dd.toInt(), Time_Hour.toInt(), Time_Minute.toInt())

    //计算目前已经过了多久
    fun getPercent(startDateStr: String, endDateStr: String): Double {
        // 将字符串转换为 LocalDate
        val startDate = LocalDate.parse(startDateStr, formatter_YYYY_MM_DD)
        val endDate = LocalDate.parse(endDateStr, formatter_YYYY_MM_DD)
        // 计算总天数和已过天数
        val totalDays = endDate.toEpochDay() - startDate.toEpochDay()
        val pastDays = today.toEpochDay() - startDate.toEpochDay()
        // 计算百分比
        val percentage = (pastDays.toDouble() / totalDays.toDouble()) * 100
        return percentage
    }
    //计算目前已经过了多久
    fun getPercentTime(startTimeStr: String, endTimeStr: String): Double {
        val startTime = LocalTime.parse(startTimeStr, formatterTime_HH_MM)
        val endTime = LocalTime.parse(endTimeStr, formatterTime_HH_MM)

        val totalSeconds = Duration.between(startTime, endTime).seconds.toDouble()
        val passedSeconds = Duration.between(startTime, currentTime).seconds.toDouble()

        val percentage = (passedSeconds / totalSeconds).coerceIn(0.0, 1.0)
        return percentage
    }
    fun getPassedMinutesInRange(startTimeStr: String, endTimeStr: String): Int? {
        val startTime = LocalTime.parse(startTimeStr, formatterTime_HH_MM)
        val endTime = LocalTime.parse(endTimeStr, formatterTime_HH_MM)
        val now = LocalTime.now()
        return when {
            now.isBefore(startTime) -> 0
            now.isAfter(endTime) -> null
            else -> Duration.between(startTime, currentTime).toMinutes().toInt()
        }
    }


    //<0是已完成 >0未完成
    fun compareTime(endTime: String,startTime : String = Time_HH_MM): TimeState {
        // 将字符串转换为 LocalTime 格式
        try {
            val last = LocalTime.parse(endTime, formatterTime_HH_MM)
            val now = LocalTime.parse(startTime, formatterTime_HH_MM)

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
    //<0是已完成 >0未完成
    fun compareTimeDate(endTime: String,startTime : String = Date_yyyy_MM_dd): TimeState {
        // 将字符串转换为 LocalTime 格式
        try {
            val last = LocalTime.parse(endTime, formatter_YYYY_MM_DD)
            val now = LocalTime.parse(startTime, formatter_YYYY_MM_DD)

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

    // 空 则用初始化的时间作为now，若想传入最新时间，需要自己在外面更新，用updateTime方法
    fun getTimeState(startTime: String, endTime: String,nowTime : String? = null): TimeState {
        try {
            val start = LocalTime.parse(startTime, formatterTime_HH_MM)
            val end = LocalTime.parse(endTime, formatterTime_HH_MM)
            val now = LocalTime.parse(nowTime ?: Time_HH_MM, formatterTime_HH_MM)

            return when {
                now.isBefore(start) -> TimeState.NOT_STARTED // 当前时间早于开始时间
                now.isAfter(end) -> TimeState.ENDED         // 当前时间晚于结束时间
                else -> TimeState.ONGOING                  // 当前时间在开始和结束之间
            }
        } catch (e : Exception) {
            return TimeState.NOT_STARTED
        }
    }

    fun daysBetween(dateString: String): Long {
        val inputDate = LocalDate.parse(dateString, formatter_YYYY_MM_DD)
        // 计算两个日期之间的天数
        return ChronoUnit.DAYS.between(today, inputDate)
    }

    fun getToday() = today

    fun updateTime(formatter : DateTimeFormatter = formatterTime_HH_MM, onChange : (String) -> Unit) = onChange(LocalDateTime.now().format(formatter))

    fun isTodayAnniversary(dateString: String): Boolean {
        return try {
            val inputMonthDay = MonthDay.parse(dateString, formatter_MM_DD)
            val todayMonthDay = MonthDay.from(today)
            inputMonthDay == todayMonthDay
        } catch (e: Exception) {
            false // 日期格式不对时，返回 false
        }
    }

    fun isCurrentMonth(yearMonthString: String): Boolean {
        return try {
            val inputYearMonth = YearMonth.parse(yearMonthString, formatter_YYYY_MM)
            val currentYearMonth = YearMonth.now()
            inputYearMonth == currentYearMonth
        } catch (e: Exception) {
            false // 格式错误时返回 false
        }
    }
    fun isOnWeekend() = dayWeek == 6 || dayWeek == 0
}

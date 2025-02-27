package com.hfut.manage.logic.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

object GetDate {
  //  val json = prefs.getString("Course",MyApplication.NullTotal)
   // val result = Gson().fromJson(json,CourseTotalResponse::class.java).result
   // val start = result.start.substringBefore(" ")
 //   @RequiresApi(Build.VERSION_CODES.O)
 //   val firstWeekStart: LocalDate = LocalDate.parse(start)
    @RequiresApi(Build.VERSION_CODES.O)
    val today: LocalDate = LocalDate.now()
  ///  @RequiresApi(Build.VERSION_CODES.O)
 // //  val weeksBetween = ChronoUnit.WEEKS.between(firstWeekStart, today) + 1

   // @RequiresApi(Build.VERSION_CODES.O)
   // var Bianhuaweeks = weeksBetween  //切换周数
   // @RequiresApi(Build.VERSION_CODES.O)
  //  val Benweeks = weeksBetween  //固定本周
    val Date_yyyy_MM = SimpleDateFormat("yyyy-MM").format(Date())

    val Date_MM_dd = SimpleDateFormat("MM-dd").format(Date())

    val Date_MM = SimpleDateFormat("MM").format(Date())

    val Date_dd = SimpleDateFormat("dd").format(Date())

    val Date_yyyy = SimpleDateFormat("yyyy").format(Date())


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



    @RequiresApi(Build.VERSION_CODES.O)
    val tomorrow = today.plusDays(1).format(DateTimeFormatter.ofPattern("MM-dd"))

}
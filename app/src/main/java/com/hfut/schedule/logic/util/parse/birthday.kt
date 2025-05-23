package com.hfut.schedule.logic.util.parse

import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.sys.DateTimeUtils.formatter_YYYY_MM_DD
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getAPICelebration
import com.hfut.schedule.ui.screen.home.getHolidays
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import java.time.LocalDate
import java.time.Period

// 恭喜你来到了浪漫的一篇

// 生日
fun getBirthday() : String? {
    return try {
        // 71 04 62 2995 13 32 999 9
        val id = getPersonInfo().chineseID
        val birth = id?.substring(6,14) ?: return null
        val year = birth.substring(0,4)
        val month = birth.substring(4,6)
        val day = birth.substring(6,8)
        "$year-$month-$day"
    } catch (e : Exception) {
        null
    }
}
// 年龄
private fun getAge(birthString : String) : Int? = try {
    val birthDate = LocalDate.parse(birthString, formatter_YYYY_MM_DD)
    val today = LocalDate.now()
    val age = Period.between(birthDate, today).years
    age
} catch (e: Exception) {
    null
}

fun getUserAge() : Int? = getBirthday()?.let { getAge(it) }

fun getAppAge() : Int? = getAge(DateTimeUtils.APP_BIRTHDAY)
// 毕业时间
private fun getGraduationYear() : String? = try {
    getPersonInfo().endDate?.split("-")?.get(0)
} catch (e : Exception) {
    null
}

// 阳历生日
fun isUserBirthday() : Boolean = getBirthday()?.let {
    DateTimeUtils.isTodayAnniversary(
        it.substringAfter(
            "-"
        )
    )
} == true
// 毕业季
fun isInGraduation() : Boolean = getGraduationYear()?.let { DateTimeUtils.isCurrentMonth("$it-06") } == true
// APP周年
fun isAppBirthday() : Boolean = DateTimeUtils.isTodayAnniversary(DateTimeUtils.APP_BIRTHDAY.substringAfter("-"))
// 节假日
fun isHoliday() : Boolean = getHolidays().any { it.isOffDay && it.date == DateTimeUtils.Date_yyyy_MM_dd }
//
fun isHolidayTomorrow() : Boolean = getHolidays().any { it.isOffDay && it.date == DateTimeUtils.tomorrow_YYYY_MM_DD }
// 今天调休
fun isSpecificWorkDay() : Boolean = getHolidays().any { !it.isOffDay && it.date == DateTimeUtils.Date_yyyy_MM_dd }
// 明天调休
fun isSpecificWorkDayTomorrow() : Boolean = getHolidays().any { !it.isOffDay && it.date == DateTimeUtils.tomorrow_YYYY_MM_DD }

fun getTodayHoliday(): String? = getHolidays().firstOrNull { it.isOffDay && it.date == DateTimeUtils.Date_yyyy_MM_dd }?.name

data class Celebration(val use : Boolean,val str : String?,val time : Long = 1L)

fun getCelebration() : Celebration {
    if(isUserBirthday()) {
        return Celebration(true,"${getUserAge()}岁生日快乐", 4L)
    }
    if(isInGraduation()) {
        return Celebration(true,"毕业季 前程似锦",2L)
    }
    if(isAppBirthday()) {
        return Celebration(true,"聚在工大${getAppAge()}周年",1L)
    }
    getTodayHoliday()?.let {
        return Celebration(true,it,1L)
    }
    if(getAPICelebration()) {
        return Celebration(true,null,1L)
    }
    return Celebration(false,null)
}
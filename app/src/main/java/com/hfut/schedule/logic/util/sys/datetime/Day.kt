package com.hfut.schedule.logic.util.sys.datetime

import com.hfut.schedule.logic.util.network.MyApiParse.getAPICelebration
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.formatter_YYYY_MM_DD
import com.hfut.schedule.ui.screen.home.getHolidays
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.common.logic.util.LogUtil
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
        LogUtil.error(e)
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
    LogUtil.error(e)
    null
}

fun getUserAge() : Int? = getBirthday()?.let { getAge(it) }

fun getAppAge() : Int? = getAge(DateTimeManager.APP_BIRTHDAY)
// 毕业时间
private fun getGraduationYear() : String? = try {
    getPersonInfo().endDate?.split("-")?.get(0)
} catch (e : Exception) {
    LogUtil.error(e)
    null
}

// 阳历生日
fun isUserBirthday() : Boolean = getBirthday()?.let {
    DateTimeManager.isTodayAnniversary(
        it.substringAfter(
            "-"
        )
    )
} == true
// 新生landing期 7~9月
fun isInLanding() : Boolean {
    val sId = getPersonInfo().getStudentIdFinally()
    if(sId?.startsWith(DateTimeManager.Date_yyyy) == true) {
        val year = sId.substring(0,4)
        return year.let {
            DateTimeManager.isCurrentMonth("$it-07")
                    || DateTimeManager.isCurrentMonth("$it-08")
                    || DateTimeManager.isCurrentMonth("$it-09")
                    || DateTimeManager.isCurrentMonth("$it-10")
        }
    } else {
        return false
    }
}
// 大学阶段：未入学 学中 已毕业
enum class UniversityPeriod {
    READY,GOING,GRADUATED
}
internal fun getUniversityPeriod(startDate: String, endDate: String): UniversityPeriod {
    val percent = DateTimeManager.getPercent(startDate, endDate)
    return if(percent in 0.0..<1.0) {
        UniversityPeriod.GOING
    } else if(percent < 0f) {
        UniversityPeriod.READY
    } else {
        UniversityPeriod.GRADUATED
    }
}
fun getUniversityPeriod() : UniversityPeriod? {
    val info = getPersonInfo()
    try {
        return getUniversityPeriod(info.startDate!!, info.endDate!!)
    } catch (e : Exception) {
        LogUtil.error(e)
        return null
    }
}
// 毕业季 5~6月
fun isInGraduation() : Boolean = getGraduationYear()?.let { DateTimeManager.isCurrentMonth("$it-05") || DateTimeManager.isCurrentMonth("$it-06") } == true
// APP周年
fun isAppBirthday() : Boolean = DateTimeManager.isTodayAnniversary(DateTimeManager.APP_BIRTHDAY.substringAfter("-"))
// 节假日
fun isHoliday() : Boolean = getHolidays().any { it.isOffDay && it.date == DateTimeManager.Date_yyyy_MM_dd }
//
fun isHolidayTomorrow() : Boolean = getHolidays().any { it.isOffDay && it.date == DateTimeManager.tomorrow_YYYY_MM_DD }
// 今天调休
fun isSpecificWorkDay() : Boolean = getHolidays().any { !it.isOffDay && it.date == DateTimeManager.Date_yyyy_MM_dd }
// 明天调休
fun isSpecificWorkDayTomorrow() : Boolean = getHolidays().any { !it.isOffDay && it.date == DateTimeManager.tomorrow_YYYY_MM_DD }

fun getTodayHoliday(): String? = getHolidays().firstOrNull { it.isOffDay && it.date == DateTimeManager.Date_yyyy_MM_dd }?.name
//    ?.substringBefore("节")

data class Celebration(val use : Boolean,val str : String?,val time : Long = 1L)

fun getCelebration() : Celebration {
    if(isUserBirthday()) {
        return Celebration(true,"生日快乐",2L)
    }
    if(isAppBirthday()) {
        return Celebration(true,"${getAppAge()}周年",0L)
    }
    getTodayHoliday()?.let {
        return Celebration(true,it,0L)
    }
    when(getUniversityPeriod()) {
        UniversityPeriod.READY -> {
            return Celebration(true,"迎新季",0L)
        }
        UniversityPeriod.GRADUATED -> {
            return Celebration(true,"欢迎回来",0L)
        }
        else -> Unit
    }
    if(isInGraduation()) {
        return Celebration(true,"毕业季",0L)
    }
    if(getAPICelebration()) {
        return Celebration(true,null,1L)
    }
    return Celebration(false,null)
}

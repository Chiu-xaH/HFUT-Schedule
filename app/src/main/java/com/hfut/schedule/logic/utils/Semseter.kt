package com.hfut.schedule.logic.utils

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.beans.jxglstu.semester
import com.hfut.schedule.logic.utils.Semseter.parseSemseter

object Semseter {
    enum class TermPeriod {
        DEFAULT,UP,DOWN
    }
    fun parseSemseter(semster : Int) : String {
        val codes = (semster - 4) / 10
        val year = 2017
        val code = 3

        var upordown = 0
        if(codes % 4 == 1) {
            upordown = 2
        } else if(codes % 4 == 3) {
            upordown = 1
        }

        val years= (year + (codes - code) / 4) + 1
        return years.toString() +  "~" + (years + 1).toString() + "年第" +  upordown + "学期"
    }
    // ((semster-4)/10)-3)/4 + 2018 = firstYear
    // ((firstYear - 2018)*4 + 3)*10 + 4 = semster
    // 传入YYYY-MM
    fun reverseGetSemester(date : String): Int? {
        // YYYY年的2~7月为 (YYYY-1)~YYYY 第2学期
        // YYYY年的8~12为 YYYY~(YYYY+1) 第1学期
        // YYYY年的1月为 (YYYY-1)~YYYY 第1学期
        try {
            val str = date.split("-")
            val year = str[0].toInt()
            val month = str[1].toInt()
            // 学期判定
            var period = TermPeriod.DEFAULT
            if(month == 1) {
                period = TermPeriod.UP
            } else if(month in 2..7) {
                period = TermPeriod.DOWN
            } else if(month in 8..12) {
                period = TermPeriod.UP
            }
            // 第一个年份的判断
            var parseYear = year
            if(month in 1..7) {
                parseYear -= 1
            }
            // 基础数据
            val semster = ((parseYear - 2018)*4 + 3)*10 + 4
            return when(period) {
                TermPeriod.UP -> semster
                TermPeriod.DOWN -> semster + 20
                TermPeriod.DEFAULT -> null
            }
        } catch (e : Exception) {
            return null
        }

    }

    fun getSemseterFromCloud() : Int {
        return try {
            Gson().fromJson(SharePrefs.prefs.getString("my",""), MyAPIResponse::class.java).semesterId.toInt()
        } catch (e:Exception) {
            reverseGetSemester(DateTimeManager.Date_MM_dd) ?: 0
        }
    }
}

//fun main( ) {
////    println(Semseter.reverseGetSemester(2025,2)?.let { parseSemseter(it) })
////    println(Semseter.reverseGetSemester(2024,9)?.let { parseSemseter(it) })
////    println(Semseter.reverseGetSemester(2025,1)?.let { parseSemseter(it) })
//    println(Semseter.reverseGetSemester("2025-02")?.let { parseSemseter(it) })
//}
package com.hfut.schedule.logic.util.parse

import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getMy

object SemseterParser {
    @JvmStatic
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
    @JvmStatic
    fun reverseGetSemester(date : String): Int? {
        // YYYY年的2~7月为 (YYYY-1)~YYYY 第2学期
        // YYYY年的8~12为 YYYY~(YYYY+1) 第1学期
        // YYYY年的1月为 (YYYY-1)~YYYY 第1学期
        try {
            val str = date.split("-")
            val year = str[0].toInt()
            val month = str[1].toInt()
            // 学期判定
            var period = 0
            if(month == 1) {
                period = 1 // 第一学期
            } else if(month in 2..7) {
                period = 2 // 第二学期
            } else if(month in 8..12) {
                period = 1
            }
            // 第一个年份的判断
            var parseYear = year
            if(month in 1..7) {
                parseYear -= 1
            }
            // 基础数据
            val semster = ((parseYear - 2018)*4 + 3)*10 + 4
            return when(period) {
                1 -> semster
                2 -> semster + 20
                else -> null
            }
        } catch (e : Exception) {
            return null
        }
    }

    @JvmStatic
    fun getSemseter() : Int {
        return try {
            reverseGetSemester(DateTimeUtils.Date_yyyy_MM) ?: 0
        } catch (e:Exception) {
            getMy()!!.semesterId.toInt()
        }
    }
}

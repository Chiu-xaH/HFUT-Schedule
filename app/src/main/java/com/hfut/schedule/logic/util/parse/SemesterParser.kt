package com.hfut.schedule.logic.util.parse

import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.network.util.MyApiParse.getMy
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import kotlinx.coroutines.flow.first

object SemesterParser {
    @JvmStatic
    fun parseSemesterUpOrDown(semester : Int) : Int {
        val codes = (semester - 4) / 10
        var upoOrDown = 0
        if(codes % 4 == 1) {
            upoOrDown = 2
        } else if(codes % 4 == 3) {
            upoOrDown = 1
        }
        return  upoOrDown
    }
    @JvmStatic
    fun parseSemester(semester : Int) : String {
        val codes = (semester - 4) / 10
        val year = 2017
        val code = 3

        var upOrDown = 0
        if(codes % 4 == 1) {
            upOrDown = 2
        } else if(codes % 4 == 3) {
            upOrDown = 1
        }

        val years= (year + (codes - code) / 4) + 1
        return years.toString() +  "~" + (years + 1).toString() + "年第" +  upOrDown + "学期"
    }
    @JvmStatic
    fun parseSemesterForDormitory(semester : Int) : String {
        val codes = (semester - 4) / 10
        val year = 2017
        val code = 3

        var upOrDown = 0
        if(codes % 4 == 1) {
            upOrDown = 2
        } else if(codes % 4 == 3) {
            upOrDown = 1
        }

        val years= (year + (codes - code) / 4) + 1
        return years.toString() +  "-" + (years + 1).toString() + "学年第" + numToChinese(upOrDown) + "学期"
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
    suspend fun getSemester() : Int {
        try {
            val autoTerm = DataStoreManager.enableAutoTerm.first()
            if(autoTerm) {
                return reverseGetSemester(DateTimeManager.Date_yyyy_MM) ?: 0
            } else {
                val autoTermValue = DataStoreManager.customTermValue.first()
                return autoTermValue
            }
        } catch (e : Exception) {
            return getMy()!!.semesterId.toInt()
        }
    }
    @JvmStatic
    fun getSemesterWithoutSuspend() : Int {
        return try {
            reverseGetSemester(DateTimeManager.Date_yyyy_MM) ?: 0
        } catch (e : Exception) {
            getMy()!!.semesterId.toInt()
        }
    }

    fun plusSemester(semester: Int) : Int = semester+20
    fun subSemester(semester: Int) : Int = semester-20

}

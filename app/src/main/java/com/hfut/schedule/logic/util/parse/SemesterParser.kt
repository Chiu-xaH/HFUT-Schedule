package com.hfut.schedule.logic.util.parse

import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.network.MyApiParse.getMy
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.LanguageHelper
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.xah.shared.LogUtil
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
    fun parseSemester(semester : Int) : String? {
        if(semester <= 0) {
            return null
        }
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

        return if(LanguageHelper.isChineseLanguage()) {
            "${years}~${years + 1}年第${upOrDown}学期"
        } else {
            "Year ${years}~${years + 1} Term " + if(upOrDown == 1) "1st" else "2nd"
        }
    }

    @JvmStatic
    fun parseSemester(text: String): Int? {
        val regex = Regex("""(\d+)\s*[~\-～]\s*(\d+)\s*学?年第([一二12])学期""")
        val match = regex.find(text) ?: return null

        val startYear = match.groupValues[1].toInt()
        val termStr = match.groupValues[3]
        val term = when (termStr) {
            "一", "1" -> 1
            "二", "2" -> 2
            else -> return null
        }

        val year = 2017
        val code = 3

        val base = (startYear - (year + 1)) * 4 + code

        val codes = when (term) {
            1 -> base
            2 -> base + 2
            else -> return null
        }

        return codes * 10 + 4
    }

    fun parseSemesterSimply(semester : Int) : String {
        val codes = (semester - 4) / 10
        val year = 2017
        val code = 3

        var upOrDown = 0
        if(codes % 4 == 1) {
            upOrDown = 2
        } else if(codes % 4 == 3) {
            upOrDown = 1
        }

        val years=( (year + (codes - code) / 4) + 1) % 100

        return if(LanguageHelper.isChineseLanguage()) {
            "${years}~${years + 1}学年${if(upOrDown == 1) "上" else "下"}学期"
        } else {
            "Year ${years}~${years + 1} Term " + if(upOrDown == 1) "1st" else "2nd"
        }
    }

    /** Int → 宿舍评分专用格式，如 "2023-2024学年第一学期" (中文数字) */
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
            LogUtil.error(e)
            return null
        }
    }

    @JvmStatic
    fun getLatestSemester(): Int {
        return reverseGetSemester(DateTimeManager.Date_yyyy_MM) ?: 0
    }

    @JvmStatic
    fun isLatestSemester(semester: Int): Boolean {
        val latestSemester = getLatestSemester()
        if (latestSemester <= 0) return false
        return semester == latestSemester
    }

    @JvmStatic
    suspend fun isCurrentSemesterLatest(): Boolean {
        val currentSemester = getSemester()
        return isLatestSemester(currentSemester)
    }

    @JvmStatic
    fun parseLatestSemesterFromTerms(terms: List<String>): Int? {
        return terms.mapNotNull { parseSemester(it) }.maxOrNull()
    }

    @JvmStatic
    suspend fun getSemester() : Int {
        val autoTerm = DataStoreManager.enableAutoTerm.first()
        return if(autoTerm) {
            getLatestSemester()
        } else {
            DataStoreManager.customTermValue.first()
        }
    }

    @JvmStatic
    fun getSemesterWithoutSuspend() : Int {
        return try {
            reverseGetSemester(DateTimeManager.Date_yyyy_MM) ?: 0
        } catch (e : Exception) {
            LogUtil.error(e)
            getMy()?.semesterId?.toIntOrNull() ?: 0
        }
    }

    fun plusSemester(semester: Int) : Int = semester+20
    fun subSemester(semester: Int) : Int = semester-20

    @JvmStatic
    fun matchesSemester(termName: String, semester: Int): Boolean {
        if (semester <= 0) return true
        val parsed = parseSemester(termName)
        if (parsed != null) return parsed == semester
        val target = parseSemester(semester) ?: return false
        fun norm(s: String) = s.replace(" ", "")
            .replace("学年", "")
            .replace("一", "1").replace("二", "2")
            .replace("-", "~").replace("～", "~").replace("/", "~")
            .replace("上", "1").replace("下", "2")
        return norm(termName) == norm(target)
    }

    data class SemesterDateRange(val startYearMonth: String, val endYearMonth: String)

    @JvmStatic
    fun getSemesterDateRange(semester: Int): SemesterDateRange? {
        if (semester <= 0) return null
        val codes = (semester - 4) / 10
        val year = 2017
        val code = 3
        val years = (year + (codes - code) / 4) + 1
        val upOrDown = when (codes % 4) {
            3 -> 1
            1 -> 2
            else -> return null
        }
        return when (upOrDown) {
            1 -> SemesterDateRange("${years}-08", "${years + 1}-01")
            2 -> SemesterDateRange("${years + 1}-02", "${years + 1}-07")
            else -> null
        }
    }
}

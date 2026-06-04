package com.hfut.schedule.logic.util.parse

import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.network.MyApiParse.getMy
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.LanguageHelper
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.xah.shared.LogUtil
import kotlinx.coroutines.flow.first

object SemesterParser {

    // ==================== 学期编码说明 ====================
    // 学期用一个 Int 编码，格式为 codes*10+4
    // codes = (semester-4)/10
    // codes%4 == 3 → 第一学期（上学期，8月~次年1月）
    // codes%4 == 1 → 第二学期（下学期，2月~7月）
    // 年份推算: years = 2017 + (codes-3)/4 + 1
    // 例: 234 → codes=23, 23%4=3 → 2023~2024第一学期
    //     254 → codes=25, 25%4=1 → 2023~2024第二学期

    // ==================== Int → 学期信息 ====================

    /** Int → 上/下学期标识 (1=上学期, 2=下学期) */
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

    /** Int → 完整显示文本，如 "2023~2024年第1学期" (带阿拉伯数字) */
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

    // ==================== String → Int ====================

    /** 学期文本 → Int 编码，支持多种格式 ("2023~2024年第1学期", "2023-2024学年第一学期" 等) */
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

    // ==================== Int → 各种格式的显示文本 ====================

    /** Int → 简短显示文本，如 "23~24学年上学期" (两位数年份 + 上/下) */
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

    // ==================== 日期 → Int ====================

    // 编码公式: ((semester-4)/10-3)/4 + 2018 = firstYear
    //           ((firstYear - 2018)*4 + 3)*10 + 4 = semester
    /** 日期字符串 "YYYY-MM" → 对应学期的 Int 编码 */
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

    // ==================== 获取当前学期 ====================

    /** 获取当前学期 (suspend，读取 DataStore) */
    @JvmStatic
    suspend fun getSemester() : Int {
        val autoTerm = DataStoreManager.enableAutoTerm.first()
        if(autoTerm) {
            return reverseGetSemester(DateTimeManager.Date_yyyy_MM) ?: 0
        } else {
            val autoTermValue = DataStoreManager.customTermValue.first()
            return autoTermValue
        }
    }

    /** 获取当前学期 (非 suspend，用于非协程环境) */
    @JvmStatic
    fun getSemesterWithoutSuspend() : Int {
        return try {
            reverseGetSemester(DateTimeManager.Date_yyyy_MM) ?: 0
        } catch (e : Exception) {
            LogUtil.error(e)
            getMy()!!.semesterId.toInt()
        }
    }

    // ==================== 学期导航 ====================

    /** 切换到下一个学期 (+20) */
    fun plusSemester(semester: Int) : Int = semester+20
    /** 切换到上一个学期 (-20) */
    fun subSemester(semester: Int) : Int = semester-20

    // ==================== 匹配与筛选 ====================

    /** 判断学期文本 termName 是否匹配目标学期编码 semester，支持多种文本格式 */
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

    /** 学期对应的日期范围 (用于消费记录等按日期筛选的场景) */
    data class SemesterDateRange(val startYearMonth: String, val endYearMonth: String)

    /** Int → 学期对应的月份范围，第一学期 8月~次年1月，第二学期 2月~7月 */
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

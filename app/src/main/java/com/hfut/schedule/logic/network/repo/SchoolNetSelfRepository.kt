package com.hfut.schedule.logic.network.repo

import com.hfut.schedule.logic.model.schoolnet.SchoolNetMonthPayRecord
import com.hfut.schedule.logic.model.schoolnet.SchoolNetMonthPayResult
import com.hfut.schedule.logic.model.schoolnet.SchoolNetMonthPaySummary
import com.hfut.schedule.logic.model.schoolnet.SchoolNetSemesterUsageResult
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.network.api.SchoolNetSelfService
import com.hfut.schedule.network.impl.SchoolNetSelfServiceCreator
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.shared.LogUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import retrofit2.awaitResponse
import java.security.MessageDigest

object SchoolNetSelfRepository {

    private val service = SchoolNetSelfServiceCreator.create(SchoolNetSelfService::class.java)
    private var selfServiceLoggedIn = false
    private var semesterQueryYears: List<Int> = emptyList()

    suspend fun loginAndGetMonthPay(
        year: Int,
        holder: StateHolder<SchoolNetMonthPayResult>
    ) = launchRequestState(
        holder = holder,
        request = {
            if (selfServiceLoggedIn) {
                LogUtil.debug("SchoolNetSelf already logged in, skip login")
                service.getMonthPay(type = 1, year = year)
            } else {
                doLoginAndFetch(year)
            }
        },
        transformSuccess = { _, html ->
            parseMonthPay(html)
        }
    )

    suspend fun getMonthPayAfterLogin(
        year: Int,
        holder: StateHolder<SchoolNetMonthPayResult>
    ) = launchRequestState(
        holder = holder,
        request = {
            if (!selfServiceLoggedIn) {
                throw Exception("校园网自服务未登录，请先查询一次")
            }
            service.getMonthPay(type = 1, year = year)
        },
        transformSuccess = { _, html ->
            parseMonthPay(html)
        }
    )

    private suspend fun doLoginAndFetch(year: Int): retrofit2.Call<okhttp3.ResponseBody> {
        val account = getPersonInfo().studentId ?: throw Exception("未获取到学号")
        val rawPassword = getCardPsk() ?: throw Exception("未获取到校园卡密码")

        val loginPageHtml = service.getLoginPage()
            .awaitResponse()
            .body()
            ?.string()
            .orEmpty()

        val checkcode = parseCheckcodeOrNull(loginPageHtml)

        if (checkcode == null) {
            LogUtil.debug("SchoolNetSelf no checkcode found, assuming already logged in")
            selfServiceLoggedIn = true
            return service.getMonthPay(type = 1, year = year)
        }

        LogUtil.debug("SchoolNetSelf account=$account, checkcode=$checkcode")

        if (needCaptcha(loginPageHtml)) {
            throw Exception("校园网自服务登录需要验证码，请先网页登录一次或稍后重试")
        }

        val randomJs = service.getRandomJs()
            .awaitResponse()
            .body()
            ?.string()
            .orEmpty()
        LogUtil.debug("SchoolNetSelf random.js=${randomJs.take(500)}")

        service.getRandomCode().awaitResponse()
        LogUtil.debug("SchoolNetSelf getRandomCode done")

        val passwordMd5 = md5Lower(rawPassword)

        val loginResponse = service.loginRaw(
            body = buildLoginBody(account, passwordMd5, checkcode)
        ).awaitResponse()

        if (!loginResponse.isSuccessful) {
            throw Exception("校园网自服务登录 HTTP ${loginResponse.code()}")
        }

        val loginHtml = loginResponse.body()?.string().orEmpty()
        checkLoginResult(loginHtml)

        selfServiceLoggedIn = true
        LogUtil.debug("SchoolNetSelf login success")

        return service.getMonthPay(type = 1, year = year)
    }

    private fun buildLoginBody(
        account: String,
        passwordMd5: String,
        checkcode: String
    ): RequestBody {
        val body = "account=$account" +
                "&password=$passwordMd5" +
                "&code=" +
                "&checkcode=$checkcode" +
                "&Submit=%E7%99%BB+%E5%BD%95"

        LogUtil.debug("SchoolNetSelf login body=$body")

        return body.toRequestBody("application/x-www-form-urlencoded".toMediaType())
    }

    private fun parseCheckcodeOrNull(html: String): String? {
        return Regex("""var\s+checkcode\s*=\s*"([^"]+)"""")
            .find(html)
            ?.groupValues
            ?.getOrNull(1)
            ?.takeIf { it.isNotBlank() }
    }

    private fun needCaptcha(html: String): Boolean {
        val tryTimes = Regex("""var\s+trytimes\s*=\s*"([^"]+)"""")
            .find(html)
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()
        return tryTimes != null && tryTimes >= 3
    }

    private fun checkLoginResult(html: String) {
        val doc = Jsoup.parse(html)

        val fieldError1 = doc.selectFirst("#fielderror1")?.text()?.trim().orEmpty()
        val fieldError2 = doc.selectFirst("#fielderror2")?.text()?.trim().orEmpty()
        val redText = doc.select(".redtext").text().trim()
        val bodyText = doc.body()?.text()?.take(1000).orEmpty()
        val hasLoginForm = doc.select("form#loginform").isNotEmpty()

        LogUtil.debug("SchoolNetSelf login title=${doc.title()}")
        LogUtil.debug("SchoolNetSelf login hasLoginForm=$hasLoginForm")
        LogUtil.debug("SchoolNetSelf login fieldError1=[$fieldError1]")
        LogUtil.debug("SchoolNetSelf login fieldError2=[$fieldError2]")
        LogUtil.debug("SchoolNetSelf login redText=[$redText]")

        if (fieldError1.isNotBlank() || fieldError2.isNotBlank()) {
            throw Exception("校园网自服务登录失败：${fieldError1.ifBlank { fieldError2 }}")
        }

        if (redText.isNotBlank()) {
            throw Exception("校园网自服务登录失败：$redText")
        }

        if (hasLoginForm) {
            val error = doc.select("#fielderror1, #fielderror2, .redtext")
                .text().trim()
                .ifBlank { bodyText.take(300) }
            throw Exception("校园网自服务登录后仍停留在登录页：$error")
        }
    }

    private fun md5Lower(input: String): String {
        return MessageDigest
            .getInstance("MD5")
            .digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    private fun parseMonthPay(html: String): SchoolNetMonthPayResult {
        if (html.isBlank()) {
            selfServiceLoggedIn = false
            throw Exception("校园网历史用量接口返回空响应，登录态可能已失效，请重新查询")
        }

        val doc = Jsoup.parse(html)

        val isLoginPage = doc.selectFirst("form#loginform") != null
                || html.contains("nav_login")
                || html.contains("LoginAction.action")

        if (isLoginPage) {
            selfServiceLoggedIn = false
            throw Exception("校园网自服务登录态已失效，请重新查询")
        }

        if (doc.selectFirst("table#example") == null) {
            val title = doc.title()
            val preview = doc.text().take(200)
            throw Exception("未找到账单表格 title=[$title] preview=[$preview]")
        }

        fun String.cleanText(): String {
            return this
                .replace("\u3000", "")
                .replace("\n", "")
                .replace("\t", "")
                .trim()
        }

        fun String.toDoubleSafe(): Double {
            return cleanText().toDoubleOrNull() ?: 0.0
        }

        fun String.toIntSafe(): Int {
            return cleanText().toIntOrNull() ?: 0
        }

        val titleText = doc.selectFirst("td.t_l")?.text()?.cleanText().orEmpty()
        val year = Regex("""\((\d{4})\)""")
            .find(titleText)
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()

        val summaryCells = doc.select("table.table2 tr td")

        val summaryPairs = summaryCells
            .chunked(2)
            .mapNotNull { pair ->
                if (pair.size >= 2) {
                    pair[0].text().cleanText() to pair[1].text().cleanText()
                } else {
                    null
                }
            }

        fun summaryValueExact(label: String): String {
            return summaryPairs
                .firstOrNull { (key, _) -> key == label }
                ?.second
                .orEmpty()
        }

        fun summaryValueContains(label: String): String {
            return summaryPairs
                .firstOrNull { (key, _) -> key.contains(label) }
                ?.second
                .orEmpty()
        }

        val summary = SchoolNetMonthPaySummary(
            baseFee = summaryValueContains("基本月租").toDoubleSafe(),
            usageFee = summaryValueContains("时长/流量计费").toDoubleSafe(),
            durationMinutes = summaryValueContains("使用时长").toIntSafe(),
            flowMb = summaryValueExact("流量(MB)").toDoubleSafe()
        )

        val records = doc.select("table#example tbody tr").mapNotNull { tr ->
            val tds = tr.select("td")
            if (tds.size < 8) return@mapNotNull null

            SchoolNetMonthPayRecord(
                startDate = tds[0].text().cleanText(),
                endDate = tds[1].text().cleanText(),
                packageName = tds[2].text().cleanText(),
                baseFee = tds[3].text().toDoubleSafe(),
                usageFee = tds[4].text().toDoubleSafe(),
                durationMinutes = tds[5].text().toIntSafe(),
                flowMb = tds[6].text().toDoubleSafe(),
                billTime = tds[7].text().cleanText()
            )
        }

        if (records.isEmpty() && !html.contains("没有检索到数据")) {
            throw Exception("未解析到校园网历史用量数据")
        }

        return SchoolNetMonthPayResult(
            year = year,
            summary = summary,
            records = records
        )
    }

    suspend fun loginAndGetSemesterUsage(
        semester: Int,
        holder: StateHolder<SchoolNetSemesterUsageResult>
    ) = launchRequestState(
        holder = holder,
        request = {
            val range = SemesterParser.getSemesterDateRange(semester)
                ?: throw Exception("无法解析学期时间范围")

            val queryYears = getYearsInRange(range.startYearMonth, range.endYearMonth)
            val firstYear = queryYears.firstOrNull()
                ?: throw Exception("无法解析校园网查询年份")

            semesterQueryYears = queryYears

            if (selfServiceLoggedIn) {
                service.getMonthPay(type = 1, year = firstYear)
            } else {
                doLoginAndFetch(firstYear)
            }
        },
        transformSuccess = { _, firstHtml ->
            val range = SemesterParser.getSemesterDateRange(semester)
                ?: throw Exception("无法解析学期时间范围")

            val results = mutableListOf(parseMonthPay(firstHtml))

            for (year in semesterQueryYears.drop(1)) {
                val html = service.getMonthPay(type = 1, year = year)
                    .awaitResponse()
                    .body()
                    ?.string()
                    .orEmpty()
                results.add(parseMonthPay(html))
            }

            buildSemesterUsageResult(semester, range.startYearMonth, range.endYearMonth, results)
        }
    )

    private fun getYearsInRange(startYearMonth: String, endYearMonth: String): List<Int> {
        val startYear = startYearMonth.substringBefore("-").toIntOrNull()
        val endYear = endYearMonth.substringBefore("-").toIntOrNull()
        if (startYear == null || endYear == null) return emptyList()
        return (startYear..endYear).toList()
    }

    private fun buildSemesterUsageResult(
        semester: Int,
        startYearMonth: String,
        endYearMonth: String,
        yearResults: List<SchoolNetMonthPayResult>
    ): SchoolNetSemesterUsageResult {
        val records = yearResults
            .flatMap { it.records }
            .filter { record ->
                val month = record.startDate.take(7)
                month >= startYearMonth && month <= endYearMonth
            }
            .sortedBy { it.startDate }

        return SchoolNetSemesterUsageResult(
            semester = semester,
            startYearMonth = startYearMonth,
            endYearMonth = endYearMonth,
            totalDurationMinutes = records.sumOf { it.durationMinutes },
            totalFlowMb = records.sumOf { it.flowMb },
            records = records
        )
    }
}

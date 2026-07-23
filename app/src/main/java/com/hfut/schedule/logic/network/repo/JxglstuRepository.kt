package com.hfut.schedule.logic.network.repo

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.database.repository.ExamHistoryRepository
import com.hfut.schedule.logic.util.network.launchRequestNone
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.network.api.impl.JxglstuServiceCreator
import com.hfut.schedule.network.api.inf.JxglstuService
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.html.JxglstuExam
import com.hfut.schedule.network.api.model.response.html.JxglstuGrade
import com.hfut.schedule.network.api.model.response.html.JxglstuTermGrade
import com.hfut.schedule.network.api.model.response.html.JxglstuTransferMajorDetail
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuCourseSearchResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuCourseTime
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuCourseTimeResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLesson
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuTermLessonResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuTextbook
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuTextbookResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.JxglstuProgramResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.competition.JxglstuProgramCompetitionResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.competition.JxglstuProgramSimpleCompletionResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.select.JxglstuSelectCourseDetailResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.select.JxglstuSelectCoursePostResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.select.JxglstuSelectCourseResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.survey.JxglstuSurveyLesson
import com.hfut.schedule.network.api.model.response.json.jxglstu.survey.JxglstuSurveyQuestionsResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.survey.JxglstuSurveyResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.transfer.JxglstuTransferMajorMyApplyResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.transfer.JxglstuTransferMajorPostResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.transfer.JxglstuTransferMajorResponse
import com.hfut.schedule.network.api.repo.JxglstuRepositoryInf
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.isValidDateTime
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.updateStartDate
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.ApplyGrade
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.GradeAndRank
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.MyApplyInfoBean
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.PlaceAndTime
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.xah.common.logic.state.UiStateHolder
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object JxglstuRepository : JxglstuRepositoryInf {
    private fun createJSONService(): JxglstuService {
        return JxglstuServiceCreator.create(JxglstuService::class.java, GlobalUiStateHolder.webVpn)
    }

    private var jxglstu = createJSONService()

    override fun updateServices() {
        jxglstu = createJSONService()
    }

    override suspend fun checkJxglstuCanUse() = launchRequestNone {
        jxglstu.checkCanUse()
    }

    override suspend fun postTransfer(
        cookie: String,
        batchId: String,
        id : String,
        phoneNumber : String,
        studentId : UiStateHolder<Int>,
        postTransferResponse: UiStateHolder<String>
    ) {
        onListenStateHolderForNetwork(studentId, postTransferResponse) { sId ->
            launchRequestState(
                holder = postTransferResponse,
                request = {
                    jxglstu.postTransfer(
                        cookie = cookie,
                        redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${sId}&batchId=${batchId}&studentId=${sId}".toRequestBody(
                            "text/plain".toMediaTypeOrNull()
                        ),
                        batchId = batchId.toRequestBody("text/plain".toMediaTypeOrNull()),
                        id = id.toRequestBody("text/plain".toMediaTypeOrNull()),
                        studentID = sId.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        telephone = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                    )
                },
                transformSuccess = { _, json -> parsePostTransfer(json) }
            )
        }
    }
    @JvmStatic
    private fun parsePostTransfer(result : String) : String = try {
        var msg = ""
        if(result.contains("result")) {
            val data =  GsonInstance.fromJson(result, JxglstuTransferMajorPostResponse::class.java)
            if(data.result) {
                msg = "成功"
            } else {
                val errors = data.errors
                errors.forEach { item ->
                    msg += item.textZh + " "
                }
            }
            msg
        } else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    override suspend fun getFormCookie(
        cookie: String,
        batchId: String,
        id : String,
        studentId : UiStateHolder<Int>,
        fromCookie : UiStateHolder<String>
    ) = onListenStateHolderForNetwork(studentId, fromCookie) { sId ->
        launchRequestState(
            holder = fromCookie,
            request = {
                jxglstu.getFormCookie(
                    cookie = cookie,
                    id = id,
                    studentId = sId.toString(),
                    redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${sId}&batchId=${batchId}&studentId=${sId}",
                    batchId = batchId
                )
            },
            transformSuccess = { headers, _ -> parseFromCookie(headers) }
        )
    }
    @JvmStatic
    private fun parseFromCookie(headers : Headers) : String = try {
        headers["Set-Cookie"].toString().let {
            it.split(";")[0]
        }
    } catch (e : Exception) { throw e }

    override suspend fun cancelTransfer(
        cookie: String,
        batchId: String,
        id : String,
        studentId : UiStateHolder<Int>,
        cancelTransferResponse : UiStateHolder<Boolean>
    ) = onListenStateHolderForNetwork(studentId, cancelTransferResponse) { sId ->
        launchRequestState(
            holder = cancelTransferResponse,
            request = {
                jxglstu.cancelTransfer(
                    cookie = cookie,
                    redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${sId}&batchId=${batchId}&studentId=${sId}",
                    batchId = batchId,
                    studentId = sId.toString(),
                    applyId = id
                )
            },
            transformSuccess = { _, _ -> false },
            transformRedirect = { _ -> true }
        )
    }

    override suspend fun verify(cookie: String) = launchRequestNone {
        jxglstu.verify(cookie)
    }

    override suspend fun getSelectCourse(
        cookie: String,
        studentId : UiStateHolder<Int>,
        bizTypeIdResponse : UiStateHolder<Int>,
        selectCourseData : UiStateHolder<List<JxglstuSelectCourseResponse>>
    ) {
        onListenStateHolderForNetwork<Int, List<JxglstuSelectCourseResponse>>(studentId, selectCourseData) { sId ->
            onListenStateHolderForNetwork<Int, List<JxglstuSelectCourseResponse>>(
                bizTypeIdResponse,
                selectCourseData
            ) { bizTypeId ->
                launchRequestState(
                    request = {
                        jxglstu.getSelectCourse(bizTypeId, sId.toString(), cookie)
                    },
                    holder = selectCourseData,
                    transformSuccess = { _, json -> parseSelectedList(json) }
                )
            }
        }
    }
    @JvmStatic
    private fun parseSelectedList(json : String) : List<JxglstuSelectCourseResponse> = try {
        val courses: List<JxglstuSelectCourseResponse> = GsonInstance.fromJson(json, object : TypeToken<List<JxglstuSelectCourseResponse>>() {}.type)
        courses
    } catch (e : Exception) { throw e }

    override suspend fun getSelectCourseInfo(cookie: String, id : Int,holder : UiStateHolder<List<JxglstuSelectCourseDetailResponse>>) =
        launchRequestState(
            holder = holder,
            request = { jxglstu.getSelectCourseInfo(id, cookie) },
            transformSuccess = { _, json -> parseSelectCourseInfo(json) }
        )
    @JvmStatic
    private fun parseSelectCourseInfo(json : String) : List<JxglstuSelectCourseDetailResponse> = try {
        val courses: List<JxglstuSelectCourseDetailResponse> = GsonInstance.fromJson(json, object : TypeToken<List<JxglstuSelectCourseDetailResponse>>() {}.type)
        courses
    } catch (e : Exception) { throw e }

    fun getSCount(cookie: String,id : Int,stdCountData : MutableLiveData<String?>) {
        val call = jxglstu.getCount(id,cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                stdCountData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override suspend fun getRequestID(
        cookie: String,
        lessonId : Int,
        courseId : Int,
        type : String,
        studentId : UiStateHolder<Int>,
        requestIdData : UiStateHolder<String>
    ) {
        onListenStateHolderForNetwork<Int, String>(studentId, requestIdData) { sId ->
            launchRequestState(
                request = {
                    jxglstu.getRequestID(
                        sId.toString(),
                        lessonId.toString(),
                        courseId.toString(),
                        cookie,
                        type
                    )
                },
                holder = requestIdData,
                transformSuccess = { _, body -> body }
            )
        }
    }

    override suspend fun getSelectedCourse(
        cookie: String,
        courseId : Int,
        studentId : UiStateHolder<Int>,
        selectedData : UiStateHolder<List<JxglstuSelectCourseDetailResponse>>
    ) {
        onListenStateHolderForNetwork<Int, List<JxglstuSelectCourseDetailResponse>>(studentId, selectedData) { sId ->
            launchRequestState(
                request = {
                    jxglstu.getSelectedCourse(sId.toString(), courseId.toString(), cookie)

                },
                holder = selectedData,
                transformSuccess = { _, json -> parseSelectedCourses(json) }
            )
        }
    }
    @JvmStatic
    private fun parseSelectedCourses(json : String) : List<JxglstuSelectCourseDetailResponse> = try {
        val courses: List<JxglstuSelectCourseDetailResponse> = GsonInstance.fromJson(json, object : TypeToken<List<JxglstuSelectCourseDetailResponse>>() {}.type)
        courses
    } catch (e : Exception) { throw e }

    override suspend fun postSelect(
        cookie: String,
        requestId : String,
        studentId : UiStateHolder<Int>,
        selectResultData : UiStateHolder<Pair<Boolean, String>>
    ) {
        onListenStateHolderForNetwork<Int, Pair<Boolean, String>>(
            studentId,
            selectResultData
        ) { sId ->
            launchRequestState(
                holder = selectResultData,
                request = { jxglstu.postSelect(sId.toString(), requestId, cookie) },
                transformSuccess = { _, json -> parseSelectResult(json) }
            )
        }
    }
    @JvmStatic
    private fun parseSelectResult(json : String) : Pair<Boolean, String> = try {
        val data = GsonInstance.fromJson(json, JxglstuSelectCoursePostResponse::class.java)
        val status = data.success
        val statusText = if(status) {
            "成功"
        } else {
            data.errorMessage?.textZh ?: "失败"
        }
        Pair(status,statusText)
    } catch (e : Exception) { throw e }

    override suspend fun getTransfer(
        cookie: String,
        batchId: String,
        studentId : UiStateHolder<Int>,
        transferData : UiStateHolder<JxglstuTransferMajorResponse>
    ) = onListenStateHolderForNetwork(studentId, transferData) { sId ->
        launchRequestState(
            holder = transferData,
            request = { jxglstu.getTransfer(cookie, batchId, sId) },
            transformSuccess = { _, json -> parseTransfer(json) }
        )
    }
    @JvmStatic
    private fun parseTransfer(json : String) : JxglstuTransferMajorResponse = try {
        GsonInstance.fromJson(json, JxglstuTransferMajorResponse::class.java)
    } catch (e : Exception) { throw e }

    override suspend fun getTransferList(
        cookie: String,
        studentId : UiStateHolder<Int>,
        transferListData : UiStateHolder<List<JxglstuTransferMajorDetail>>
    ) = onListenStateHolderForNetwork(studentId, transferListData) { sId ->
        launchRequestState(
            holder = transferListData,
            request = { jxglstu.getTransferList(cookie, sId) },
            transformSuccess = { _, html -> parseTransferList(html) }
        )
    }
    @JvmStatic
    private fun parseTransferList(html : String) : List<JxglstuTransferMajorDetail> = try {
        val document = Jsoup.parse(html)
        val result = mutableListOf<JxglstuTransferMajorDetail>()

        // 获取所有的 turn-panel 元素
        val turnPanels = document.select(".turn-panel")
        for (panel in turnPanels) {
            val title = panel.select(".turn-title span").text()
            val dataValue = panel.select(".change-major-enter").attr("data")
            val applicationDate = panel.select(".open-date .text-primary").text()
            val admissionDate = panel.select(".select-date .text-warning").text()

            if (title.isNotBlank() && dataValue.isNotBlank()) {
                result.add(
                    JxglstuTransferMajorDetail(
                        title = title,
                        batchId = dataValue,
                        applicationDate = applicationDate,
                        admissionDate = admissionDate
                    )
                )
            }
        }
        result
    } catch (e : Exception) { throw e }

    override suspend fun getMyApply(
        cookie: String,
        batchId: String,
        studentId: UiStateHolder<Int>,
        myApplyData : UiStateHolder<JxglstuTransferMajorMyApplyResponse>
    ) = onListenStateHolderForNetwork(studentId, myApplyData) { sId ->
        launchRequestState(
            holder = myApplyData,
            request = { jxglstu.getMyTransfer(cookie, batchId, sId) },
            transformSuccess = { _, json -> parseMyApply(json) }
        )
    }
    @JvmStatic
    private fun parseMyApply(json: String) : JxglstuTransferMajorMyApplyResponse = try {
        GsonInstance.fromJson(json, JxglstuTransferMajorMyApplyResponse::class.java)
    } catch (e : Exception) { throw e }

    suspend fun getMyApplyInfo(
        cookie: String,
        listId: Int,
        studentId: UiStateHolder<Int>,
        myApplyInfoData : UiStateHolder<MyApplyInfoBean>
    ) = onListenStateHolderForNetwork(studentId, myApplyInfoData) { sId ->
        launchRequestState(
            holder = myApplyInfoData,
            request = { jxglstu.getMyTransferInfo(cookie, listId, sId) },
            transformSuccess = { _, html -> parseMyApplyGradeInfo(html) }
        )
    }
    @JvmStatic
    private fun parseMyApplyGradeInfo(html: String) : MyApplyInfoBean = try {
        val doc = Jsoup.parse(html)
        // 面试安排
        val interviewRow = doc.select("div.interview-arrange-1 tr:contains(面试安排)").first()
        val interviewTime = interviewRow?.select(".arrange-text:nth-of-type(1) span:nth-of-type(2)")?.text().orEmpty()
        val interviewPlace = interviewRow?.select(".arrange-text:nth-of-type(2) span:nth-of-type(2)")?.text().orEmpty()
        val interview = if (interviewTime.isNotEmpty() && interviewPlace.isNotEmpty()) {
            PlaceAndTime(interviewPlace, interviewTime)
        } else null
        // 笔试安排
        val examRow = doc.select("div.interview-arrange-1 tr:contains(笔试安排)").first()
        val examTime = examRow?.select(".arrange-text:nth-of-type(1) span:nth-of-type(2)")?.text().orEmpty()
        val examPlace = examRow?.select(".arrange-text:nth-of-type(2) span:nth-of-type(2)")?.text().orEmpty()
        val exam = if (examTime.isNotEmpty() && examPlace.isNotEmpty()) {
            PlaceAndTime(examPlace, examTime)
        } else null
        // 成绩信息
        val gpaScore = doc.select("div.score-box:has(span:contains(GPA)) span.score-text").text().toDoubleOrNull() ?: 0.0
        val gpaRank = doc.select("div.score-box:has(span:contains(GPA)) span.score-rank span").text().toIntOrNull()

        val operateAvgScore = doc.select("div.score-box:has(span:contains(算术平均分)) span.score-text").text().toDoubleOrNull() ?: 0.0
        val operateAvgRank = doc.select("div.score-box:has(span:contains(算术平均分)) span.score-rank span").text().toIntOrNull()

        val weightAvgScore = doc.select("div.score-box:has(span:contains(加权平均分)) span.score-text").text().toDoubleOrNull() ?: 0.0
        val weightAvgRank = doc.select("div.score-box:has(span:contains(加权平均分)) span.score-rank span").text().toIntOrNull()

        val transferAvgScore = doc.select("div.score-box:has(span:contains(转专业考核成绩)) span.score-text").text().toDoubleOrNull() ?: 0.0
        val transferAvgRank = doc.select("div.score-box:has(span:contains(转专业考核成绩)) span.score-rank span").text().toIntOrNull()

        val grade = ApplyGrade(
            gpa = GradeAndRank(gpaScore, gpaRank),
            operateAvg = GradeAndRank(operateAvgScore, operateAvgRank),
            weightAvg = GradeAndRank(weightAvgScore, weightAvgRank),
            transferAvg = GradeAndRank(transferAvgScore, transferAvgRank)
        )
        // 构造结果
        MyApplyInfoBean(meetSchedule = interview, examSchedule = exam, grade = grade)
    } catch (e : Exception) { throw e }

    override suspend fun getGradeFromJxglstu(
        cookie: String,
        semester: Int?,
        studentId: UiStateHolder<Int>,
        jxglstuGradeData : UiStateHolder<List<JxglstuTermGrade>>
    ) = onListenStateHolderForNetwork(studentId, jxglstuGradeData) { sId ->
        launchRequestState(
            holder = jxglstuGradeData,
            request = { jxglstu.getGrade(cookie, sId.toString(), semester) },
            transformSuccess = { _, html -> parseJxglstuGradeInner(html) }
        )
    }

    @JvmStatic
    private suspend fun parseJxglstuGradeInner(html: String): List<JxglstuTermGrade> = try {
        LargeStringDataManager.save(LargeStringDataManager.GRADE,html)
        parseJxglstuGrade(html)
    } catch (e: Exception) {
        throw e
    }

    override suspend fun parseJxglstuGrade(html: String): List<JxglstuTermGrade> =
        withContext(Dispatchers.Default) {
            try {
                val doc = Jsoup.parse(html)
                val termElements = doc.select("h3")
                val tableElements = doc.select("table.student-grade-table")

                val result = mutableListOf<JxglstuTermGrade>()

                for ((index, termElement) in termElements.withIndex()) {
                    val term = termElement.text()
                    val table = tableElements.getOrNull(index) ?: continue
                    val rows = table.select("tr")
                    val list = mutableListOf<JxglstuGrade>()

                    for (row in rows) {
                        val tds = row.select("td") // 选择tr标签下的所有td标签
                        if (!tds.isEmpty()) {
                            val titles = tds[0].text()
                            val codes = tds[2].text()
                            val scores = tds[3].text()
                            val gpa = tds[4].text()
                            val totalGrade = tds[5].text()
                            val grades = tds[6].text()
                            list.add(
                                JxglstuGrade(
                                    titles,
                                    scores,
                                    gpa,
                                    grades,
                                    totalGrade,
                                    codes
                                )
                            )
                        }
                    }
                    result.add(JxglstuTermGrade(term, list))
                }
                result
            } catch (e: Exception) {
                throw e
            }
        }

    override fun jxglstuLogin(cookie : String) {
        val call = jxglstu.jxglstulogin(cookie)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    override suspend fun getBizTypeId(cookie: String,studentId : Int,holder : UiStateHolder<Int>) =
        launchRequestState(
            holder = holder,
            request = { jxglstu.getBizTypeId(cookie, studentId) },
            transformSuccess = { _, html -> parseBizTypeId(html) }
        )
    @JvmStatic
    private fun parseBizTypeId(html : String): Int = try{
        getBizTypeId(html)!!
    } catch (e : Exception) {
        throw e
    }

    @JvmStatic
    private fun getBizTypeId(html : String) : Int? {
        val doc = Jsoup.parse(html)
        try {
            val scriptElements = doc.select("script")
            val regex = """bizTypeId\s*:\s*(\d+)""".toRegex()
            for (script in scriptElements) {
                val scriptText = script.html()
                val matchResult = regex.find(scriptText)
                if (matchResult != null) {
                    val id = matchResult.groupValues[1].toIntOrNull()
                    return id
                }
            }
        } catch (e: Exception) {
            LogUtil.error(e)
            return null
        }
        return null
    }


    override suspend fun getStudentId(cookie : String,holder : UiStateHolder<Int>) = launchRequestState(
        holder = holder,
        request = { jxglstu.getStudentId(cookie) },
        transformRedirect = { headers -> parseStudentId(headers) },
        transformSuccess = { _, _ -> -1 }
    )
    @JvmStatic
    private fun parseStudentId(headers: Headers): Int {
        val i = "/eams5-student/for-std/course-table/info/"
        try {
            if (headers["Location"].toString().contains(i)) {
                return headers["Location"].toString().substringAfter(i).toInt()
            } else if(headers["Location"].toString().contains("/login")){
                throw Exception("登陆状态失效")
            } else {
                throw Exception(headers["Location"].toString())
            }
        } catch (e : Exception) {
            throw e
        }
    }

    override suspend fun getLessonIds(cookie : String,studentId : Int,bizTypeId : Int,holder : UiStateHolder<JxglstuTermLessonResponse>) =
        launchRequestState(
            holder = holder,
            request = {
                jxglstu.getLessonIds(
                    cookie,
                    bizTypeId.toString(),
                    SemesterParser.getSemester().toString(),
                    studentId.toString()
                )
            },
            transformSuccess = { _, json -> parseLessonIds(json) }
        )
    @JvmStatic
    private suspend fun parseLessonIds(json : String) : JxglstuTermLessonResponse {
        LargeStringDataManager.save(LargeStringDataManager.getTotalCoursesKey(SemesterParser.getSemester()),json)
        updateStartDate(json)
        try {
            return GsonInstance.fromJson(json, JxglstuTermLessonResponse::class.java)
        } catch (e : Exception) { throw e }
    }

    override suspend fun getDatum(
        cookie : String,
        lessonIdList : List<Int>,
        studentId : UiStateHolder<Int>,
        datumData : UiStateHolder<String>
    ) = onListenStateHolderForNetwork(studentId, datumData) { sId ->
        val lessonIdsArray = JsonArray()
        lessonIdList.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
        val jsonObject = JsonObject().apply {
            add("lessonIds", lessonIdsArray)//课程ID
            addProperty("studentId", sId)//学生ID
            addProperty("weekIndex", "")
        }
        launchRequestState(
            holder = datumData,
            request = { jxglstu.getDatum(cookie, jsonObject) },
            transformSuccess = { _, json -> parseDatum(json) }
        )
    }
    @JvmStatic
    private suspend fun parseDatum(json : String) : String {
        if (json.contains("result")) {
            LargeStringDataManager.save(LargeStringDataManager.getJxglstuDatumKey(SemesterParser.getSemester()),json)
            try {
                return json
            } catch (e : Exception) {
                throw e
            }
        } else {
            throw Exception(json)
        }
    }

    override suspend fun getInfo(cookie : String,studentId : UiStateHolder<Int>) {
        onListenStateHolderForNetwork<Int, Unit>(studentId, null) { sId ->
            val call = jxglstu.getInfo(cookie, sId.toString())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) {
                        val html = response.body()?.string() ?: return
                        SharedPrefs.saveString("info",html)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
            val call2 = jxglstu.getMyProfile(cookie)

            call2.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if(response.isSuccessful) {
                        val html = response.body()?.string() ?: return
                        SharedPrefs.saveString("profile",html)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    override suspend fun getLessonTimes(cookie: String,timeCampusId : Int,holder : UiStateHolder<List<JxglstuCourseTime>>) =
        launchRequestState(
            holder = holder,
            request = {
                jxglstu.getLessonTimes(
                    cookie,
                    JxglstuService.LessonTimeRequest(timeCampusId)
                )
            },
            transformSuccess = { _, json -> parseLessonTimes(json) }
        )
    @JvmStatic
    private suspend fun parseLessonTimes(result: String) : List<JxglstuCourseTime> =
        withContext(Dispatchers.IO) {
            DataStoreManager.saveCourseTable(result)
            return@withContext try {
                GsonInstance.fromJson(result, JxglstuCourseTimeResponse::class.java).result.courseUnitList
            } catch (e: Exception) {
                throw e
            }
        }

    override suspend fun getProgram(
        cookie: String,
        studentId: UiStateHolder<Int>,
        programData : UiStateHolder<JxglstuProgramResponse>
    ) = onListenStateHolderForNetwork(studentId, programData) { sId ->
        launchRequestState(
            holder = programData,
            request = { jxglstu.getProgram(cookie, sId.toString()) },
            transformSuccess = { _, json -> parseProgram(json) }
        )
    }
    @JvmStatic
    private suspend fun parseProgram(result: String) : JxglstuProgramResponse {
        LargeStringDataManager.save(LargeStringDataManager.PROGRAM,result)
        return try {
            GsonInstance.fromJson(result, JxglstuProgramResponse::class.java)
        } catch (e : Exception) {
            throw e
        }
    }

    override suspend fun getProgramCompletion(cookie: String,holder : UiStateHolder<JxglstuProgramSimpleCompletionResponse>) =
        launchRequestState(
            holder = holder,
            request = { jxglstu.getProgramCompletion(cookie) },
            transformSuccess = { _, json -> parseProgramCompletion(json) }
        )
    @JvmStatic
    private fun parseProgramCompletion(json : String) : JxglstuProgramSimpleCompletionResponse = try {
        SharedPrefs.saveString("PROGRAM_COMPETITION", json)
        val listType = object : TypeToken<List<JxglstuProgramSimpleCompletionResponse>>() {}.type
        val data : List<JxglstuProgramSimpleCompletionResponse> = GsonInstance.fromJson(json, listType)
        data[0]
    } catch (e : Exception) { throw e }

    override suspend fun getProgramPerformance(
        cookie: String,
        studentId: UiStateHolder<Int>,
        programPerformanceData : UiStateHolder<JxglstuProgramCompetitionResponse>
    ) = onListenStateHolderForNetwork(studentId, programPerformanceData) { sId ->
        launchRequestState(
            holder = programPerformanceData,
            request = { jxglstu.getProgramPerformance(cookie, sId) },
            transformSuccess = { _, json -> parseProgramPerformance(json) }
        )
    }
    @JvmStatic
    private suspend fun parseProgramPerformance(json : String) : JxglstuProgramCompetitionResponse = try {
        LargeStringDataManager.save(LargeStringDataManager.PROGRAM_PERFORMANCE,json)
        GsonInstance.fromJson(json, JxglstuProgramCompetitionResponse::class.java)
    } catch (e : Exception) { throw e }

    override suspend fun searchCourse(
        cookie: String,
        className : String?,
        courseName : String?,
        semester : Int,
        courseId : String?,
        studentId: UiStateHolder<Int>,
        courseSearchResponse : UiStateHolder<List<JxglstuLesson>>
    ) = onListenStateHolderForNetwork(studentId, courseSearchResponse) { sId ->
        launchRequestState(
            holder = courseSearchResponse,
            request = {
                jxglstu.searchCourse(
                    cookie,
                    sId.toString(),
                    semester,
                    className,
                    "1,${Constant.DEFAULT_PAGE_SIZE}",
                    courseName,
                    courseId
                )
            },
            transformSuccess = { _, json -> parseSearchCourse(json) }
        )
    }
    @JvmStatic
    private fun parseSearchCourse(result : String) : List<JxglstuLesson> = try {
        GsonInstance.fromJson(result, JxglstuCourseSearchResponse::class.java).data.map { it.lesson }
    } catch (e : Exception) { throw e }

    override suspend fun getSurveyList(
        cookie: String,
        semester : Int,
        studentId: UiStateHolder<Int>,
        surveyListData : UiStateHolder<List<JxglstuSurveyLesson>>
    ) = onListenStateHolderForNetwork(studentId, surveyListData) { sId ->
        launchRequestState(
            holder = surveyListData,
            request = {
                jxglstu.getSurveyList(cookie, sId.toString(), semester)
            },
            transformSuccess = { _, json -> parseSurveyList(json) }
        )
    }
    @JvmStatic
    private fun parseSurveyList(json : String) : List<JxglstuSurveyLesson> = try {
        GsonInstance.fromJson(json, JxglstuSurveyResponse::class.java).list
    } catch (e : Exception) { throw e }

    override suspend fun getSurvey(cookie: String, id : String,holder : UiStateHolder<JxglstuSurveyQuestionsResponse>) =
        launchRequestState(
            holder = holder,
            request = { jxglstu.getSurveyInfo(cookie, id) },
            transformSuccess = { _, json -> parseSurvey(json) }
        )
    @JvmStatic
    private fun parseSurvey(json : String) : JxglstuSurveyQuestionsResponse = try {
        GsonInstance.fromJson(json, JxglstuSurveyQuestionsResponse::class.java)
    } catch (e : Exception) { throw e }

    override suspend fun getSurveyToken(
        cookie: String,
        id : String,
        studentId : UiStateHolder<Int>,
        surveyToken : UiStateHolder<String>
    ) = onListenStateHolderForNetwork(studentId, surveyToken) { sId ->
        launchRequestState(
            holder = surveyToken,
            request = {
                jxglstu.getSurveyToken(
                    cookie,
                    id,
                    "/for-std/lesson-survey/semester-index/${sId}"
                )
            },
            transformSuccess = { headers, _ -> parseSurveyToken(headers) }
        )
    }
    @JvmStatic
    private fun parseSurveyToken(headers : Headers) : String = try {
        headers.toString().substringAfter("Set-Cookie:").substringAfter("set-cookie:").substringBefore(";")
    } catch(e : Exception) { throw e }

    override suspend fun postSurvey(cookie : String, json: JsonObject) = launchRequestNone {
        jxglstu.postSurvey(cookie, json)
    }

    override suspend fun getPhoto(cookie : String,studentId : UiStateHolder<Int>) =
        withContext(Dispatchers.IO) {
            onListenStateHolderForNetwork<Int, Unit>(studentId, null) { sId ->
                val call = jxglstu.getPhoto(cookie, sId.toString())

                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        launch { savePhoto(response) }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
        }

    private suspend fun savePhoto(response: Response<ResponseBody>) = try {
        val bytes = response.body()?.bytes()
        // 将字节数组转换为Base64编码的字符串
        val base64String = Base64.encodeToString(bytes, Base64.DEFAULT)
        // 保存编码后的字符串
        LargeStringDataManager.save(LargeStringDataManager.PHOTO,base64String)
    } catch (e: Exception) {
        LogUtil.error(e)
    }

    override suspend fun getCourseBook(
        cookie: String,
        semester: Int,
        studentId: UiStateHolder<Int>,
        bizTypeIdResponse: UiStateHolder<Int>,
        courseBookResponse : UiStateHolder<Pair<Int, Map<Long, JxglstuTextbook>>>
    ) = onListenStateHolderForNetwork(studentId, courseBookResponse) { sId ->
        onListenStateHolderForNetwork(bizTypeIdResponse, courseBookResponse) { bizTypeId ->
            launchRequestState(
                holder = courseBookResponse,
                request = {
                    jxglstu.getCourseBook(
                        cookie,
                        bizTypeId = bizTypeId,
                        semesterId = semester,
                        studentId = sId
                    )
                },
                transformSuccess = { _, json -> parseCourseBookNetwork(json, semester) }
            )
        }
    }
    @JvmStatic
    private suspend fun parseCourseBookNetwork(json : String,semester : Int) : Pair<Int,Map<Long, JxglstuTextbook>> = try {
        val gson = GsonInstance
        val data = gson.fromJson(json, JxglstuTextbookResponse::class.java).textbookAssignMap
//        val originMapJson = LargeStringDataManager.read(LargeStringDataManager.BOOK_INFO)
//        val originMap = originMapJson?.let {
//            val type = object : TypeToken<Map<String, CourseBookBean>>() {}.type
//            val data: Map<String, CourseBookBean> = gson.fromJson(it, type)
//            data
//        } ?: emptyMap()
        // 将JSON以String只保存data部分 增量保存
//        val finalMap = originMap + data
//        val dataJson = gson.toJson(finalMap)
        val dataJson = gson.toJson(data)
        LargeStringDataManager.save(LargeStringDataManager.getBookKey(SemesterParser.getSemester()),dataJson)
        Pair(semester,parseCourseBook(json))
    } catch (e : Exception) { throw e }

    override fun parseCourseBook(json: String) : Map<Long, JxglstuTextbook> = try {
        val type = object : TypeToken<Map<String, JxglstuTextbook>>() {}.type
        val data: Map<String, JxglstuTextbook> = GsonInstance.fromJson(json, type)
        // 键为id，与课程汇总对接
        // 将键转换为Long
        data.mapNotNull { (key, value) ->
            key.toLongOrNull()?.let { longKey ->
                longKey to value
            }
        }.toMap()
    } catch (e : Exception) {
        LogUtil.error(e)
        emptyMap()
    }

    override fun parseDatumCourse(result: String) : List<JxglstuLesson> = try {
        GsonInstance.fromJson(result, JxglstuTermLessonResponse::class.java).lessons
    } catch (e : Exception) {
        LogUtil.error(e)
        emptyList<JxglstuLesson>()
    }


    override suspend fun getExam(cookie: String, studentId : UiStateHolder<Int>, examHolder : UiStateHolder<List<JxglstuExam>>) {
        onListenStateHolderForNetwork<Int, Unit>(studentId, null) { sId ->
            launchRequestState(
                holder = examHolder,
                transformSuccess = { _, html -> parseJxglstuExamInner(html) },
                request = { jxglstu.getExam(cookie, sId.toString()) }
            )
        }
    }

    override suspend fun parseJxglstuExam(html : String) : List<JxglstuExam> = try {
        LargeStringDataManager.save(LargeStringDataManager.EXAM,html)
        val doc = Jsoup.parse(html).select("tbody tr")
        val data = doc.map { row ->
            val elements = row.select("td")
            JxglstuExam(
                name = elements[0].text(),
                dateTime = elements[1].text(),
                place = elements[2].text()
            )
        }

        val filteredData = data
            .filter { isValidDateTime(it.dateTime) }
            .sortedBy { it.dateTime }
        filteredData
    } catch (e:Exception) { throw e }

    @JvmStatic
    private suspend fun parseJxglstuExamInner(html : String) : List<JxglstuExam> = try {
        LargeStringDataManager.save(LargeStringDataManager.EXAM,html)
        val exams = parseJxglstuExam(html)
        try {
            ExamHistoryRepository.saveExamSnapshot(
                exams = exams,
                source = "jxglstu",
                fallbackSemester = SemesterParser.getLatestSemester()
            )
        } catch (e: Exception) {
            LogUtil.error(e, "保存教务系统考试历史失败")
        }
        exams
    } catch (e:Exception) { throw e }
}

package com.hfut.schedule.logic.network.repo


import com.hfut.schedule.logic.database.repository.ExamHistoryRepository
import com.xah.common.logic.model.Campus
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppProgramResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppExamResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppBuilding
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppBuildingResponse
import com.hfut.schedule.network.api.util.getUinAppCampusId
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppProgramData
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassmate
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassmateResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassroomCourse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassroomCourseTableResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppCourse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppCourseTableResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppEmptyClassroom
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppEmptyClassroomResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppGrade
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppGradeResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppLoginResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassroomSearchResult
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassroomSearchResponse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppProgramSearchData
import com.hfut.schedule.logic.util.network.launchRequestNone
import com.hfut.schedule.logic.util.network.launchRequestState
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.impl.UniAppServiceCreator
import com.hfut.schedule.network.api.inf.UniAppService
import com.hfut.schedule.network.api.model.request.uniapp.UniAppEmptyClassroomRequest
import com.hfut.schedule.network.api.model.request.uniapp.UniAppSearchProgramRequest
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.util.CryptoUtil
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.network.core.StatusCode
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppProgramSearchResponse
import com.hfut.schedule.ui.screen.home.cube.sub.getJxglstuPassword
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.parseJxglstuIntTime
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.JxglstuExam
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.isValidDateTime
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.common.logic.util.LogUtil
import retrofit2.awaitResponse

object UniAppRepository {
    private val uniApp = UniAppServiceCreator.create(UniAppService::class.java)
    private const val FAILED_TEXT = "登陆合工大教务失败"

    suspend fun login() : Boolean {
        try {
            val sId = getPersonInfo().getStudentIdFinally()
            val pwd = getJxglstuPassword()
            if(pwd == null || sId == null) {
                showToast("$FAILED_TEXT(游客)")
                return false
            }
            showToast("正在登录合工大教务")
            val request = uniApp.login(
                studentId = sId,
                password = CryptoUtil.rsaEncrypt(pwd)
            ).awaitResponse()
            val json = request.body()?.string()
            if(json == null) {
                showToast(FAILED_TEXT)
                return false
            }
            if(!request.isSuccessful) {
                val msg = parseLogin(json,false)
                showToast("$FAILED_TEXT$msg")
                return false
            }
            val token = parseLogin(json,true)
            if(token == null) {
                showToast("${FAILED_TEXT}2")
                return false
            }
            DataStoreManager.saveUniAppJwt(token)
            showToast("登陆合工大教务成功")
            return true
        } catch (e : Exception) {
            LogUtil.error(e)
            e.message?.let { showToast(it) } ?: showToast("登录失败，可能是服务器问题，稍后再试")
            return false
        }
    }
    @JvmStatic
    private fun parseLogin(
        json : String,
        isSuccessful : Boolean
    ) : String? = try {
        if(isSuccessful) {
            GsonInstance.fromJson(json, UniAppLoginResponse.UniAppLoginSuccessResponse::class.java).data.idToken
        } else {
            GsonInstance.fromJson(json, UniAppLoginResponse.UniAppLoginFailResponse::class.java).message
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }

    suspend fun getClassmates(
        lessonId : String,
        token : String ,
        holder : UiStateHolder<List<UniAppClassmate>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getClassmates(lessonId, token) },
        transformSuccess = { _, json -> parseClassmates(json) }
    )

    @JvmStatic
    private fun parseClassmates(json : String) = try {
        GsonInstance.fromJson(json, UniAppClassmateResponse::class.java).data ?: emptyList()
    } catch (e : Exception) { throw e }

    suspend fun updateCourses(token : String) {
        try {
            val request = uniApp.getCourses(SemesterParser.getSemester(),token).awaitResponse()
            if(!request.isSuccessful) {
                return
            }
            val json = request.body()?.string() ?: return
            LargeStringDataManager.save(LargeStringDataManager.getUniAppCoursesKey(SemesterParser.getSemester()),json)
        } catch (e : Exception) {
            LogUtil.error(e)
        }
    }

    @JvmStatic
    suspend fun parseUniAppCourses(jStr : String? = null) :  List<UniAppCourse> {
        val json = LargeStringDataManager.read(
            LargeStringDataManager.getUniAppCoursesKey(
                SemesterParser.getSemester())) ?: jStr
        return try {
            GsonInstance.fromJson(json, UniAppCourseTableResponse::class.java).data
        } catch (e : Exception) {
            LogUtil.error(e)
            emptyList()
        }
    }

    suspend fun getGrades(
        token : String ,
        holder : UiStateHolder<Map<String, List<UniAppGrade>>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getGrades(token) },
        transformSuccess = { _, json -> parseGrades(json) }
    )

    @JvmStatic
    private fun parseGrades(json : String) : Map<String, List<UniAppGrade>> = try {
        val originalList = GsonInstance.fromJson(json, UniAppGradeResponse::class.java).data
        // 按列表项目的term进行分类
        val finalList = mutableMapOf<String, MutableList<UniAppGrade>>()
        originalList.forEach { item ->
            finalList.getOrPut(item.semester.nameZh) { mutableListOf() }.add(item.copy(
                gradeDetail = item.gradeDetail.replace(';',' ')
            ))
        }
        finalList
    } catch (e : Exception) { throw e }

    suspend fun updateExams(token : String) {
        try {
            var request = uniApp.getExams(token).awaitResponse()
            // 登陆过期，重新刷新一次登录
            if(request.code() == StatusCode.UNAUTHORIZED.code) {
                LogUtil.debug("合工大教务登陆过期")
                val result = login()
                if(result) {
                    request = uniApp.getExams(token).awaitResponse()
                }
            }
            if(!request.isSuccessful) {
                return
            }
            val json = request.body()?.string() ?: return
            LargeStringDataManager.save(LargeStringDataManager.UNI_APP_EXAMS,json)
            try {
                ExamHistoryRepository.saveExamSnapshot(
                    exams = parseExamsForHistory(json),
                    source = "uniapp",
                    fallbackSemester = SemesterParser.getLatestSemester()
                )
            } catch (e: Exception) {
                LogUtil.error(e, "保存 UniApp 考试历史失败")
            }
        } catch (e : Exception) {
            LogUtil.error(e)
        }
    }

    private fun parseExamsForHistory(json: String): List<JxglstuExam> {
        return GsonInstance.fromJson(json, UniAppExamResponse::class.java).data.mapNotNull {
            val startTime = parseJxglstuIntTime(it.startTime)
            val endTime = parseJxglstuIntTime(it.endTime)
            val dateTime = "${it.examDate} ${startTime}~${endTime}"
            if (!isValidDateTime(dateTime)) {
                null
            } else {
                JxglstuExam(
                    name = it.courseNameZh.trim(),
                    dateTime = dateTime,
                    place = it.place?.substringAfterLast(" "),
                    type = it.examType.nameZh
                )
            }
        }
    }

    suspend fun searchPrograms(
        token : String,
        page : Int ,
        keyword : String = "",
        holder : UiStateHolder<List<UniAppProgramSearchData>>
    ) = launchRequestState(
        holder = holder,
        request = {
            uniApp.searchPrograms(
                UniAppSearchProgramRequest(
                    nameZhLike = keyword,
                    pageSize = Constant.DEFAULT_PAGE_SIZE,
                    currentPage = page,
                ), token
            )
        },
        transformSuccess = { _, json -> parseProgramSearch(json) }
    )

    @JvmStatic
    private fun parseProgramSearch(json : String) : List<UniAppProgramSearchData> = try {
        GsonInstance.fromJson(json, UniAppProgramSearchResponse::class.java).data.data
    } catch (e : Exception) { throw e }

    suspend fun getProgramById(
        id : Int,
        token: String,
        holder : UiStateHolder<UniAppProgramData>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getProgramById(id, token) },
        transformSuccess = { _, json -> parseProgramSearchInfo(json) }
    )

    @JvmStatic
    private fun parseProgramSearchInfo(json : String) : UniAppProgramData = try {
        GsonInstance.fromJson(json, UniAppProgramResponse::class.java).data
    } catch (e : Exception) { throw e }

    suspend fun getBuildings(
        token : String,
        holder : UiStateHolder<List<UniAppBuilding>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getBuildings(token) },
        transformSuccess = { _, json -> parseBuildings(json) }
    )
    @JvmStatic
    private fun parseBuildings(json : String) : List<UniAppBuilding> = try {
        val originalList = GsonInstance.fromJson(json, UniAppBuildingResponse::class.java).data
        val codeList = Campus.entries.map {
            getUinAppCampusId(it)
        }
        val result = originalList.filter { it.campusAssoc in codeList }
        result.map { item ->
            item.copy(
                nameZh = item.nameZh
                    .replace("（宣城）","")
                    .replace("(宣)",""),
            )
        }
    } catch (e : Exception) { throw e }

    suspend fun getEmptyClassrooms(
        page : Int,
        date : String,
        campus: Campus?,
        buildings : List<Int>?,
        floors : List<Int>?,
        token : String,
        holder : UiStateHolder<List<UniAppEmptyClassroom>>
    ) = launchRequestState(
        holder = holder,
        request = {
            uniApp.getEmptyClassrooms(
                UniAppEmptyClassroomRequest(
                    currentPage = page,
                    date = date,
                    campusAssoc = campus?.let { getUinAppCampusId(it) },
                    buildingIds = buildings,
                    floors = floors
                ), token
            )
        },
        transformSuccess = { _, json -> parseEmptyClassrooms(json) }
    )
    @JvmStatic
    private fun parseEmptyClassrooms(json : String) : List<UniAppEmptyClassroom>  = try {
        GsonInstance.fromJson(json, UniAppEmptyClassroomResponse::class.java).data.data
    } catch (e : Exception) { throw e }

    suspend fun searchClassrooms(
        input : String,
        token : String,
        page : Int,
        holder : UiStateHolder<List<UniAppClassroomSearchResult>>
    ) = launchRequestState(
        request = {
            uniApp.searchClassrooms(
                input,
                "${page},${Constant.DEFAULT_PAGE_SIZE}",
                token
            )
        },
        holder = holder,
        transformSuccess = { _, json -> parseSearchClassrooms(json) }
    )
    @JvmStatic
    private fun parseSearchClassrooms(json : String) = try {
        GsonInstance.fromJson(json, UniAppClassroomSearchResponse::class.java).data.data
    } catch (e : Exception) { throw e }

    suspend fun getClassroomLessons(
        semester: Int,
        roomId : Int,
        token : String,
        holder : UiStateHolder<List<UniAppClassroomCourse>>
    ) = launchRequestState(
        request = { uniApp.getClassroomLessons(semester, roomId, token) },
        holder = holder,
        transformSuccess = { _, json -> parseClassroomLessons(json) }
    )
    @JvmStatic
    private fun parseClassroomLessons(json : String) = try {
        GsonInstance.fromJson(json, UniAppClassroomCourseTableResponse::class.java).data
    } catch (e : Exception) { throw e }


    suspend fun checkLogin(
        token : String
    ) = launchRequestNone {
        uniApp.getExams(token)
    }
}

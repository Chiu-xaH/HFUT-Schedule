package com.hfut.schedule.logic.network.repo.hfut

import com.google.gson.Gson
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.Campus.*
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchResponse
import com.hfut.schedule.logic.model.uniapp.UniAppClassmatesBean
import com.hfut.schedule.logic.model.uniapp.UniAppClassmatesResponse
import com.hfut.schedule.logic.model.uniapp.UniAppBuildingBean
import com.hfut.schedule.logic.model.uniapp.UniAppBuildingsResponse
import com.hfut.schedule.logic.model.uniapp.UniAppCampus
import com.hfut.schedule.logic.model.uniapp.UniAppClassroomLessonBean
import com.hfut.schedule.logic.model.uniapp.UniAppClassroomLessonsResponse
import com.hfut.schedule.logic.model.uniapp.UniAppEmptyClassroomBean
import com.hfut.schedule.logic.model.uniapp.UniAppEmptyClassroomRequest
import com.hfut.schedule.logic.model.uniapp.UniAppEmptyClassroomResponse
import com.hfut.schedule.logic.model.uniapp.UniAppCourseBean
import com.hfut.schedule.logic.model.uniapp.UniAppCoursesResponse
import com.hfut.schedule.logic.model.uniapp.UniAppGradeBean
import com.hfut.schedule.logic.model.uniapp.UniAppGradesResponse
import com.hfut.schedule.logic.model.uniapp.UniAppLoginResponse.UniAppLoginError
import com.hfut.schedule.logic.model.uniapp.UniAppLoginResponse.UniAppLoginSuccessfulResponse
import com.hfut.schedule.logic.model.uniapp.UniAppSearchClassroomBean
import com.hfut.schedule.logic.model.uniapp.UniAppSearchClassroomsResponse
import com.hfut.schedule.logic.model.uniapp.UniAppSearchProgramBean
import com.hfut.schedule.logic.model.uniapp.UniAppSearchProgramRequest
import com.hfut.schedule.logic.model.uniapp.UniAppSearchProgramResponse
import com.hfut.schedule.logic.network.api.UniAppService
import com.hfut.schedule.logic.network.servicecreator.UniAppServiceCreator
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.logic.util.network.getPageSize
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.cube.sub.getJxglstuPassword
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.uicommon.util.LogUtil
import retrofit2.awaitResponse

object UniAppRepository {
    private val uniApp = UniAppServiceCreator.create(UniAppService::class.java)
    private const val FAILED_TEXT = "登陆合工大教务失败"

    suspend fun login() : Boolean {
        val sId = getPersonInfo().studentId
        val pwd = getJxglstuPassword()
        if(pwd == null || sId == null) {
            showToast("$FAILED_TEXT(游客)")
            return false
        }
        val request = uniApp.login(
            studentId = sId,
            password = Crypto.rsaEncrypt(pwd)
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
    }
    @JvmStatic
    private fun parseLogin(
        json : String,
        isSuccessful : Boolean
    ) : String? = try {
        if(isSuccessful) {
            Gson().fromJson(json,UniAppLoginSuccessfulResponse::class.java).data.idToken
        } else {
            Gson().fromJson(json,UniAppLoginError::class.java).message
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }

    suspend fun getClassmates(
        lessonId : String,
        token : String ,
        holder : StateHolder<List<UniAppClassmatesBean>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getClassmates(lessonId,token) },
        transformSuccess = { _,json -> parseClassmates(json) }
    )

    @JvmStatic
    private fun parseClassmates(json : String) = try {
        Gson().fromJson(json, UniAppClassmatesResponse::class.java).data ?: emptyList()
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
    suspend fun parseUniAppCourses(jStr : String? = null) :  List<UniAppCourseBean> {
        val json = LargeStringDataManager.read(LargeStringDataManager.getUniAppCoursesKey(SemesterParser.getSemester())) ?: jStr
        return try {
            Gson().fromJson(json, UniAppCoursesResponse::class.java).data
        } catch (e : Exception) {
            LogUtil.error(e)
            emptyList()
        }
    }

    suspend fun getGrades(
        token : String ,
        holder : StateHolder<Map<String, List<UniAppGradeBean>>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getGrades(token) },
        transformSuccess = { _,json -> parseGrades(json) }
    )

    @JvmStatic
    private fun parseGrades(json : String) : Map<String, List<UniAppGradeBean>> = try {
        val originalList = Gson().fromJson(json, UniAppGradesResponse::class.java).data
        // 按列表项目的term进行分类
        val finalList = mutableMapOf<String, MutableList<UniAppGradeBean>>()
        originalList.forEach { item ->
            finalList.getOrPut(item.semester.nameZh) { mutableListOf() }.add(item.copy(
                gradeDetail = item.gradeDetail.replace(';',' ')
            ))
        }
        finalList
    } catch (e : Exception) { throw e }

    suspend fun updateExams(token : String) {
        try {
            val request = uniApp.getExams(token).awaitResponse()
            if(!request.isSuccessful) {
                return
            }
            val json = request.body()?.string() ?: return
            LargeStringDataManager.save(LargeStringDataManager.UNI_APP_EXAMS,json)
        } catch (e : Exception) {
            LogUtil.error(e)
        }
    }

    suspend fun searchPrograms(
        token : String,
        page : Int ,
        keyword : String = "",
        holder : StateHolder<List<UniAppSearchProgramBean>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.searchPrograms(UniAppSearchProgramRequest(
            nameZhLike = keyword,
            pageSize = getPageSize(),
            currentPage = page,
        ),token)},
        transformSuccess = { _,json -> parseProgramSearch(json) }
    )

    @JvmStatic
    private fun parseProgramSearch(json : String) : List<UniAppSearchProgramBean> = try {
        Gson().fromJson(json, UniAppSearchProgramResponse::class.java).data.data
    } catch (e : Exception) { throw e }

    suspend fun getProgramById(
        id : Int,
        token: String,
        holder : StateHolder<ProgramSearchBean>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getProgramById(id,token) },
        transformSuccess = { _, json -> parseProgramSearchInfo(json) }
    )

    @JvmStatic
    private fun parseProgramSearchInfo(json : String) : ProgramSearchBean = try {
        Gson().fromJson(json,ProgramSearchResponse::class.java).data
    } catch (e : Exception) { throw e }

    suspend fun getBuildings(
        token : String,
        holder : StateHolder<List<UniAppBuildingBean>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getBuildings(token) },
        transformSuccess = { _,json -> parseBuildings(json) }
    )
    @JvmStatic
    private fun parseBuildings(json : String) : List<UniAppBuildingBean> = try {
        val originalList = Gson().fromJson(json, UniAppBuildingsResponse::class.java).data
        val codeList = UniAppCampus.entries.map { it.code }
        originalList.filter { it.campusAssoc in codeList }
    } catch (e : Exception) { throw e }

    suspend fun getEmptyClassrooms(
        page : Int,
        date : String,
        campus: Campus?,
        buildings : List<Int>?,
        floors : List<Int>?,
        token : String,
        holder : StateHolder<List<UniAppEmptyClassroomBean>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getEmptyClassrooms(
            UniAppEmptyClassroomRequest(
                currentPage = page,
                date = date,
                campusAssoc = when(campus) {
                    XC -> UniAppCampus.XC.code
                    FCH -> UniAppCampus.FCH.code
                    TXL -> UniAppCampus.TXL.code
                    null -> null
                },
                buildingIds = buildings,
                floors = floors
            ),token
        ) },
        transformSuccess = { _,json -> parseEmptyClassrooms(json) }
    )
    @JvmStatic
    private fun parseEmptyClassrooms(json : String) : List<UniAppEmptyClassroomBean>  = try {
        Gson().fromJson(json, UniAppEmptyClassroomResponse::class.java).data.data
    } catch (e : Exception) { throw e }

    suspend fun searchClassrooms(
        input : String,
        token : String,
        page : Int,
        holder : StateHolder<List<UniAppSearchClassroomBean>>
    ) = launchRequestState(
        request = { uniApp.searchClassrooms(input,"${page},${getPageSize()}",token) },
        holder = holder,
        transformSuccess = { _,json -> parseSearchClassrooms(json) }
    )
    @JvmStatic
    private fun parseSearchClassrooms(json : String) = try {
        Gson().fromJson(json, UniAppSearchClassroomsResponse::class.java).data.data
    } catch (e : Exception) { throw e }

    suspend fun getClassroomLessons(
        semester: Int,
        roomId : Int,
        token : String,
        holder : StateHolder<List<UniAppClassroomLessonBean>>
    ) = launchRequestState(
        request = { uniApp.getClassroomLessons(semester,roomId,token) },
        holder = holder,
        transformSuccess = { _,json -> parseClassroomLessons(json) }
    )
    @JvmStatic
    private fun parseClassroomLessons(json : String) = try {
        Gson().fromJson(json, UniAppClassroomLessonsResponse::class.java).data
    } catch (e : Exception) { throw e }
}


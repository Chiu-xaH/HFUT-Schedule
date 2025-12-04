package com.hfut.schedule.logic.network.repo.hfut

import com.google.gson.Gson
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchResponse
import com.hfut.schedule.logic.model.uniapp.ClassmatesBean
import com.hfut.schedule.logic.model.uniapp.ClassmatesResponse
import com.hfut.schedule.logic.model.uniapp.UniAppCourseBean
import com.hfut.schedule.logic.model.uniapp.UniAppCoursesResponse
import com.hfut.schedule.logic.model.uniapp.UniAppGradeBean
import com.hfut.schedule.logic.model.uniapp.UniAppGradesResponse
import com.hfut.schedule.logic.model.uniapp.UniAppLoginResponse.UniAppLoginError
import com.hfut.schedule.logic.model.uniapp.UniAppLoginResponse.UniAppLoginSuccessfulResponse
import com.hfut.schedule.logic.model.uniapp.UniAppSearchProgramBean
import com.hfut.schedule.logic.model.uniapp.UniAppSearchProgramRequest
import com.hfut.schedule.logic.model.uniapp.UniAppSearchProgramResponse
import com.hfut.schedule.logic.network.api.UniAppService
import com.hfut.schedule.logic.network.servicecreator.UniAppServiceCreator
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.logic.util.network.getPageSize
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.cube.sub.getJxglstuPassword
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import retrofit2.awaitResponse

object UniAppRepository {
    private val uniApp = UniAppServiceCreator.create(UniAppService::class.java)
    private val failedText = "登陆合工大教务失败"

    suspend fun login() : Boolean {
        val sId = getPersonInfo().studentId
        val pwd = getJxglstuPassword()
        if(pwd == null || sId == null) {
            showToast("$failedText(游客)")
            return false
        }
        val request = uniApp.login(
            studentId = sId,
            password = Crypto.rsaEncrypt(pwd)
        ).awaitResponse()
        val json = request.body()?.string()
        if(json == null) {
            showToast(failedText)
            return false
        }
        if(!request.isSuccessful) {
            val msg = parseLogin(json,false)
            showToast("$failedText$msg")
            return false
        }
        val token = parseLogin(json,true)
        if(token == null) {
            showToast("${failedText}2")
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
        e.printStackTrace()
        null
    }

    suspend fun getClassmates(
        lessonId : String,
        token : String ,
        holder : StateHolder<List<ClassmatesBean>>
    ) = launchRequestState(
        holder = holder,
        request = { uniApp.getClassmates(lessonId,token) },
        transformSuccess = { _,json -> parseClassmates(json) }
    )

    @JvmStatic
    private fun parseClassmates(json : String) = try {
        Gson().fromJson(json, ClassmatesResponse::class.java).data ?: emptyList()
    } catch (e : Exception) { throw e }

    suspend fun updateCourses(token : String) {
        try {
            val request = uniApp.getCourses(SemseterParser.getSemseter(),token).awaitResponse()
            if(!request.isSuccessful) {
                return
            }
            val json = request.body()?.string() ?: return
            LargeStringDataManager.save(LargeStringDataManager.UNI_APP_COURSES,json)
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    suspend fun parseUniAppCourses(jStr : String? = null) :  List<UniAppCourseBean> {
        val json = LargeStringDataManager.read( LargeStringDataManager.UNI_APP_COURSES) ?: jStr
        return try {
            Gson().fromJson(json, UniAppCoursesResponse::class.java).data
        } catch (e : Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
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
}


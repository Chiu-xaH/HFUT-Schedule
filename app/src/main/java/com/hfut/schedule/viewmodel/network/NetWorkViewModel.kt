package com.hfut.schedule.viewmodel.network

import android.util.Base64
import android.util.Log
import androidx.camera.core.Logger.e
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.enumeration.AdmissionType
import com.hfut.schedule.logic.enumeration.LibraryItems
import com.hfut.schedule.logic.enumeration.LoginType
import com.hfut.schedule.logic.model.AcademicNewsResponse
import com.hfut.schedule.logic.model.AcademicType
import com.hfut.schedule.logic.model.AcademicXCType
import com.hfut.schedule.logic.model.AdmissionDetailBean
import com.hfut.schedule.logic.model.AdmissionMapBean
import com.hfut.schedule.logic.model.AdmissionTokenResponse
import com.hfut.schedule.logic.model.ForecastAllBean
import com.hfut.schedule.logic.model.HaiLeDeviceDetailBean
import com.hfut.schedule.logic.model.HaiLeDeviceDetailRequestBody
import com.hfut.schedule.logic.model.HaiLeNearPositionBean
import com.hfut.schedule.logic.model.HaiLeNearPositionRequestDTO
import com.hfut.schedule.logic.model.HaiLeTradeBean
import com.hfut.schedule.logic.model.HaiLeTradeListRequestDTO
import com.hfut.schedule.logic.model.NewsResponse
import com.hfut.schedule.logic.model.PayData
import com.hfut.schedule.logic.model.PayResponse
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.model.QWeatherWarnBean
import com.hfut.schedule.logic.model.SupabaseEventEntity
import com.hfut.schedule.logic.model.SupabaseEventForkCount
import com.hfut.schedule.logic.model.SupabaseEventOutput
import com.hfut.schedule.logic.model.SupabaseEventsInput
import com.hfut.schedule.logic.model.SupabaseLoginResponse
import com.hfut.schedule.logic.model.SupabaseRefreshLoginBean
import com.hfut.schedule.logic.model.SupabaseUserLoginBean
import com.hfut.schedule.logic.model.TeacherResponse
import com.hfut.schedule.logic.model.WorkSearchResponse
import com.hfut.schedule.logic.model.XuanquResponse
import com.hfut.schedule.logic.model.community.ApplyFriendResponse
import com.hfut.schedule.logic.model.community.ApplyingLists
import com.hfut.schedule.logic.model.community.ApplyingResponse
import com.hfut.schedule.logic.model.community.AvgResult
import com.hfut.schedule.logic.model.community.BookPositionBean
import com.hfut.schedule.logic.model.community.BookPositionResponse
import com.hfut.schedule.logic.model.community.BorrowRecords
import com.hfut.schedule.logic.model.community.BorrowResponse
import com.hfut.schedule.logic.model.community.DormitoryBean
import com.hfut.schedule.logic.model.community.DormitoryInfoResponse
import com.hfut.schedule.logic.model.community.DormitoryResponse
import com.hfut.schedule.logic.model.community.DormitoryUser
import com.hfut.schedule.logic.model.community.FailRateRecord
import com.hfut.schedule.logic.model.community.FailRateResponse
import com.hfut.schedule.logic.model.community.GradeAllResponse
import com.hfut.schedule.logic.model.community.GradeAllResult
import com.hfut.schedule.logic.model.community.GradeAvgResponse
import com.hfut.schedule.logic.model.community.GradeJxglstuDTO
import com.hfut.schedule.logic.model.community.GradeResponse
import com.hfut.schedule.logic.model.community.GradeResponseJXGLSTU
import com.hfut.schedule.logic.model.community.GradeResult
import com.hfut.schedule.logic.model.community.LibRecord
import com.hfut.schedule.logic.model.community.LibraryResponse
import com.hfut.schedule.logic.model.community.MapBean
import com.hfut.schedule.logic.model.community.MapResponse
import com.hfut.schedule.logic.model.community.StuAppBean
import com.hfut.schedule.logic.model.community.StuAppsResponse
import com.hfut.schedule.logic.model.community.TodayResponse
import com.hfut.schedule.logic.model.community.TodayResult
import com.hfut.schedule.logic.model.jxglstu.CourseBookBean
import com.hfut.schedule.logic.model.jxglstu.CourseBookResponse
import com.hfut.schedule.logic.model.jxglstu.CourseSearchResponse
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.model.jxglstu.LessonTimesResponse
import com.hfut.schedule.logic.model.jxglstu.MyApplyResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramBean
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramListBean
import com.hfut.schedule.logic.model.jxglstu.ProgramResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchBean
import com.hfut.schedule.logic.model.jxglstu.ProgramSearchResponse
import com.hfut.schedule.logic.model.jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.model.jxglstu.SurveyResponse
import com.hfut.schedule.logic.model.jxglstu.SurveyTeacherResponse
import com.hfut.schedule.logic.model.jxglstu.TransferResponse
import com.hfut.schedule.logic.model.jxglstu.forStdLessonSurveySearchVms
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.model.one.BorrowBooksResponse
import com.hfut.schedule.logic.model.one.SubBooksResponse
import com.hfut.schedule.logic.model.one.getTokenResponse
import com.hfut.schedule.logic.model.zjgd.BillBean
import com.hfut.schedule.logic.model.zjgd.BillMonth
import com.hfut.schedule.logic.model.zjgd.BillMonthResponse
import com.hfut.schedule.logic.model.zjgd.BillRangeResponse
import com.hfut.schedule.logic.model.zjgd.BillResponse
import com.hfut.schedule.logic.model.zjgd.ChangeLimitResponse
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.model.zjgd.FeeType.ELECTRIC_XUANCHENG
import com.hfut.schedule.logic.model.zjgd.FeeType.NET_XUANCHENG
import com.hfut.schedule.logic.model.zjgd.FeeType.SHOWER_XUANCHENG
import com.hfut.schedule.logic.model.zjgd.PayStep1Response
import com.hfut.schedule.logic.model.zjgd.PayStep2Response
import com.hfut.schedule.logic.model.zjgd.PayStep3Response
import com.hfut.schedule.logic.network.api.CommunityService
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.OneService
import com.hfut.schedule.logic.network.api.StuService
import com.hfut.schedule.logic.network.api.SupabaseService
import com.hfut.schedule.logic.network.api.ZJGDBillService
import com.hfut.schedule.logic.network.repo.Repository
import com.hfut.schedule.logic.network.repo.Repository.launchRequestSimple
import com.hfut.schedule.logic.network.servicecreator.CommunitySreviceCreator
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuHTMLServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.servicecreator.MyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneGotoServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneServiceCreator
import com.hfut.schedule.logic.network.servicecreator.StuServiceCreator
import com.hfut.schedule.logic.network.servicecreator.SupabaseServiceCreator
import com.hfut.schedule.logic.network.servicecreator.ZJGDBillServiceCreator
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.state.CasInHFUT
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.network.supabaseEventDtoToEntity
import com.hfut.schedule.logic.util.network.supabaseEventEntityToDto
import com.hfut.schedule.logic.util.network.supabaseEventForkDtoToEntity
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.WebInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.ApplyGrade
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus.HEFEI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus.XUANCHENG
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.ChangeMajorInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.GradeAndRank
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.MyApplyInfoBean
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.PlaceAndTime
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.TransferPostResponse
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.screen.home.search.function.one.mail.MailResponse
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

// 106个函数
class NetWorkViewModel(var webVpn: Boolean) : ViewModel() {
    private fun createJSONService(): JxglstuService {
        return JxglstuJSONServiceCreator.create(JxglstuService::class.java, webVpn)
    }

    private fun createHTMLService(): JxglstuService {
        return JxglstuHTMLServiceCreator.create(JxglstuService::class.java, webVpn)
    }
    private var jxglstuJSON = createJSONService()
    private var jxglstuHTML = createHTMLService()
    private val oneGoto = OneGotoServiceCreator.create(LoginService::class.java)
    private val one = OneServiceCreator.create(OneService::class.java)
    private val huiXin = ZJGDBillServiceCreator.create(ZJGDBillService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)
    private val community = CommunitySreviceCreator.create(CommunityService::class.java)
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
    private val myAPI = MyServiceCreator.create(MyService::class.java)
    private val stu = StuServiceCreator.create(StuService::class.java)
    private val supabase = SupabaseServiceCreator.create(SupabaseService::class.java)
    fun updateServices() {
        jxglstuJSON = createJSONService()
        jxglstuHTML = createHTMLService()
    }
    val studentId = StateHolder<Int>()
    val lessonIds = StateHolder<lessonResponse>()
    val token = MutableLiveData<String>()


    val haiLeNearPositionResp = StateHolder<List<HaiLeNearPositionBean>>()
    suspend fun getHaiLeNearPosition(bean : HaiLeNearPositionRequestDTO) = Repository.getHaiLeNear(bean,haiLeNearPositionResp)

    val haiLeDeviceDetailResp = StateHolder<List<HaiLeDeviceDetailBean>>()
    suspend fun getHaiLeDeviceDetail(bean : HaiLeDeviceDetailRequestBody) = Repository.getHaiLDeviceDetail(bean,haiLeDeviceDetailResp)

    val haiLeTradeListResp = StateHolder<List<HaiLeTradeBean>>()
    suspend fun getHaiLeTradeList(bean : HaiLeTradeListRequestDTO) = Repository.getHaiLTradeList(bean,haiLeTradeListResp)

    val githubStarsData = StateHolder<Int>()
    suspend fun getStarNum() = Repository.getStarNum(githubStarsData)

    val workSearchResult = StateHolder<WorkSearchResponse>()
    suspend fun searchWorks(keyword: String?, page: Int = 1,type: Int,campus: Campus) = Repository.searchWorks(keyword,page,type,campus,workSearchResult)
// Supabase ////////////////////////////////////////////////////////////////////////////////////////////////
    val supabaseTodayVisitResp = StateHolder<Int>()
    val supabaseUserCountResp = StateHolder<Int>()

    suspend fun getTodayVisit() = launchRequestSimple(
        holder = supabaseTodayVisitResp,
        request = { supabase.getTodayVisitCount().awaitResponse() },
        transformSuccess = { _,body -> parseTodayVisit(body) }
    )
    private fun parseTodayVisit(body : String) : Int = try {
        body.toInt()
    } catch (e : Exception) { throw e }

    suspend fun getUserCount() = launchRequestSimple(
        holder = supabaseUserCountResp,
        request = { supabase.getUserCount().awaitResponse() },
        transformSuccess = { _,body -> parseTodayVisit(body) }
    )


    val supabaseRegResp = MutableLiveData<String?>()
    fun supabaseReg(password: String) = Repository.makeRequest(supabase.reg(user = SupabaseUserLoginBean(password = password)),supabaseRegResp)

    val supabaseLoginResp = StateHolder<SupabaseLoginResponse>()
    suspend fun supabaseLoginWithPassword(password : String) = launchRequestSimple(
        holder = supabaseLoginResp,
        request = { supabase.login(user = SupabaseUserLoginBean(password = password), loginType = "password").awaitResponse() },
        transformSuccess = { _,json -> parseRefreshTokenSupabase(json) }
    )

    suspend fun supabaseLoginWithRefreshToken(refreshToken : String) = launchRequestSimple(
        holder = supabaseLoginResp,
        request = { supabase.login(user = SupabaseRefreshLoginBean(refreshToken), loginType = "refresh_token").awaitResponse() },
        transformSuccess = { _,json -> parseRefreshTokenSupabase(json) }
    )
    private fun parseRefreshTokenSupabase(json : String) : SupabaseLoginResponse = try {
        Gson().fromJson(json, SupabaseLoginResponse::class.java)
    } catch (e : Exception) { throw e }

    val supabaseDelResp = StateHolder<Boolean>()
    suspend fun supabaseDel(jwt : String,id : Int) = launchRequestSimple(
        holder = supabaseDelResp,
        request = { supabase.delEvent(authorization = "Bearer $jwt",id = "eq.$id").awaitResponse() },
        transformSuccess = { _,_ -> true }
    )

    val admissionTokenResp = StateHolder<AdmissionTokenResponse>()
    suspend fun getAdmissionToken() = Repository.getAdmissionToken(admissionTokenResp)

    val admissionListResp = StateHolder<Pair<AdmissionType,Map<String, List<AdmissionMapBean>>>>()
    suspend fun getAdmissionList(type: AdmissionType) = Repository.getAdmissionList(type,admissionListResp)

    val admissionDetailResp = StateHolder<AdmissionDetailBean>()
    suspend fun getAdmissionDetail(type : AdmissionType,bean : AdmissionMapBean,region: String) = Repository.getAdmissionDetail(type,bean,region,admissionDetailResp,admissionTokenResp)

    val supabaseAddResp = MutableLiveData<Pair<Boolean,String?>?>()
    fun supabaseAdd(jwt: String,event : SupabaseEventOutput) {
        val call = supabase.addEvent(authorization = "Bearer $jwt",entity = supabaseEventDtoToEntity(event))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                supabaseAddResp.value = Pair(response.isSuccessful,response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    val supabaseAddCountResp = MutableLiveData<Boolean?>()
    fun supabaseAddCount(jwt: String,eventId : Int) {
        val call = supabase.eventDownloadAdd(authorization = "Bearer $jwt",entity = supabaseEventForkDtoToEntity(eventId))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                supabaseAddCountResp.value = response.isSuccessful
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    // 默认 展示未过期日程&&符合自己班级的日程
    val supabaseGetEventsResp = MutableLiveData<String?>()
    fun supabaseGetEvents(jwt: String) = Repository.makeRequest(supabase.getEvents(authorization = "Bearer $jwt"),supabaseGetEventsResp)

    private val _eventForkCountCache = mutableStateMapOf<Int, String>()
    val eventForkCountCache: Map<Int, String> get() = _eventForkCountCache
    fun supabaseGetEventForkCount(jwt: String, eventId: Int) {
        if(_eventForkCountCache.containsKey(eventId)) {
            return
        }
        val call = supabase.getEventDownloadCount(authorization = "Bearer $jwt", entity = SupabaseEventForkCount(eventId = eventId))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    val count = response.body()?.string()
                    count?.let { _eventForkCountCache[eventId] = count }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    val supabaseGetEventCountResp = StateHolder<String?>()
    suspend fun supabaseGetEventCount(jwt: String) = launchRequestSimple(
        holder = supabaseGetEventCountResp,
        request = { supabase.getEventCount(authorization = "Bearer $jwt").awaitResponse() },
        transformSuccess = { _,body -> body }
    )

    val supabaseGetEventLatestResp = StateHolder<Boolean>()
    suspend fun supabaseGetEventLatest(jwt: String) = launchRequestSimple(
        holder = supabaseGetEventLatestResp,
        request = { supabase.getEventLatestTime(authorization = "Bearer $jwt").awaitResponse() },
        transformSuccess = { _,body -> parseSupabaseLatestEventTime(body) }
    )
    private fun parseSupabaseLatestEventTime(body : String) : Boolean {
        try {
            if(prefs.getString("SUPABASE_LATEST",null) == body) {
                // 没有新的日程
                return false
            } else {
                // 有新的日程
                // 保存
                saveString("SUPABASE_LATEST",body)
                return true
            }
        } catch (e : Exception) { throw e }
    }

    // 定制 展示自己上传过的日程
    val supabaseGetMyEventsResp = StateHolder<List<SupabaseEventsInput>>()
    suspend fun supabaseGetMyEvents(jwt: String) = launchRequestSimple(
        holder = supabaseGetMyEventsResp,
        request = { supabase.getEvents(authorization = "Bearer $jwt",endTime = null,email = "eq." + getSchoolEmail()).awaitResponse() },
        transformSuccess = { _,json -> parseSupabaseMyEvents(json) }
    )
    private fun parseSupabaseMyEvents(json : String) : List<SupabaseEventsInput> = try {
        val list : List<SupabaseEventEntity> = Gson().fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
        list.mapNotNull { item -> supabaseEventEntityToDto(item) }
    } catch(e : Exception) { throw e }

    val supabaseCheckResp = StateHolder<Boolean>()
    suspend fun supabaseCheckJwt(jwt: String) = launchRequestSimple(
        holder = supabaseCheckResp,
        request = { supabase.checkToken(authorization = "Bearer $jwt").awaitResponse() },
        transformSuccess = { _,_ -> true }
    )

    val supabaseUpdateResp = StateHolder<Boolean>()
    suspend fun supabaseUpdateEvent(jwt: String, id: Int, body : Map<String,Any>) = launchRequestSimple(
        holder = supabaseUpdateResp,
        request = { supabase.updateEvent(authorization = "Bearer $jwt",id = "eq.$id", body = body).awaitResponse() },
        transformSuccess = { _, _ -> true }
    )
// 培养方案 ////////////////////////////////////////////////////////////////////////////////////////////////

    var programList = StateHolder<List<ProgramListBean>>()
    suspend fun getProgramList(campus : Campus) = launchRequestSimple(
        holder = programList,
        request = { myAPI.getProgramList(
            when(campus) {
                HEFEI -> "hefei"
                XUANCHENG -> "xuancheng"
            }
        ).awaitResponse() },
        transformSuccess = { _,json -> parseProgramSearch(json)}
    )
    private fun parseProgramSearch(json : String) : List<ProgramListBean> = try {
        val data: List<ProgramListBean> = Gson().fromJson(json,object : TypeToken<List<ProgramListBean>>() {}.type)
        data
    } catch (e : Exception) { throw e }

    val programSearchData = StateHolder<ProgramSearchBean>()
    suspend fun getProgramListInfo(id : Int,campus : Campus) = launchRequestSimple(
        holder = programSearchData,
        request = { myAPI.getProgram(
            id,
            when(campus) {
                HEFEI -> "hefei"
                XUANCHENG -> "xuancheng"
            }
        ).awaitResponse() },
        transformSuccess = { _,json -> parseProgramSearchInfo(json) }
    )
    private fun parseProgramSearchInfo(json : String) : ProgramSearchBean = try {
        Gson().fromJson(json,ProgramSearchResponse::class.java).data
    } catch (e : Exception) { throw e }

    fun downloadHoliday()  = Repository.downloadHoliday()

    val postTransferResponse = StateHolder<String>()
    suspend fun postTransfer(cookie: String, batchId: String, id : String, phoneNumber : String) {
        onListenStateHolderForNetwork(studentId,postTransferResponse) { sId ->
            launchRequestSimple(
                holder = postTransferResponse,
                request = {
                    jxglstuJSON.postTransfer(
                        cookie = cookie,
                        redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${sId}&batchId=${batchId}&studentId=${sId}".toRequestBody(
                            "text/plain".toMediaTypeOrNull()
                        ),
                        batchId = batchId.toRequestBody("text/plain".toMediaTypeOrNull()),
                        id = id.toRequestBody("text/plain".toMediaTypeOrNull()),
                        studentID = sId.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull()),
                        telephone = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                    ).awaitResponse()
                },
                transformSuccess = { _, json -> parsePostTransfer(json) }
            )
        }
    }
    private fun parsePostTransfer(result : String) : String = try {
        var msg = ""
        if(result.contains("result")) {
            val data =  Gson().fromJson(result,TransferPostResponse::class.java)
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

    val fromCookie = StateHolder<String>()
    suspend fun getFormCookie(
        cookie: String,
        batchId: String,
        id : String,
    ) = onListenStateHolderForNetwork(studentId,fromCookie) { sId ->
        launchRequestSimple(
            holder = fromCookie,
            request = {
                jxglstuHTML.getFormCookie(
                    cookie = cookie,
                    id = id,
                    studentId = sId.toString(),
                    redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${sId}&batchId=${batchId}&studentId=${sId}",
                    batchId = batchId
                ).awaitResponse()
            },
            transformSuccess = { headers,_ -> parseFromCookie(headers) }
        )
    }
    private fun parseFromCookie(headers : Headers) : String = try {
        headers["Set-Cookie"].toString().let {
            it.split(";")[0]
        }
    } catch (e : Exception) { throw e }

    // 响应 为 302代表成功 否则为失败
    val cancelTransferResponse = StateHolder<Boolean>()
    suspend fun cancelTransfer(
        cookie: String,
        batchId: String,
        id : String,
    ) = onListenStateHolderForNetwork(studentId,cancelTransferResponse) { sId ->
        launchRequestSimple(
            holder = cancelTransferResponse,
            request = {
                jxglstuJSON.cancelTransfer(
                    cookie = cookie,
                    redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${sId}&batchId=${batchId}&studentId=${sId}",
                    batchId = batchId,
                    studentId = sId.toString(),
                    applyId = id
                ).awaitResponse()
            },
            transformSuccess = { _, json -> false },
            transformRedirect = { _ -> true }
        )
    }

    fun postUser() {
        val call = supabase.postUsage()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

// 选课 ////////////////////////////////////////////////////////////////////////////////////////////////
    val verifyData = MutableLiveData<String?>()
    fun verify(cookie: String) {
        val call = jxglstuJSON.verify(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    verifyData.value = response.code().toString()
                } else {
                    verifyData.value = response.code().toString()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    val selectCourseData = MutableLiveData<String?>()
    suspend fun getSelectCourse(cookie: String) {
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            onListenStateHolderForNetwork<Int,Unit>(bizTypeIdResponse,null) { bizTypeId ->
                val call = jxglstuJSON.getSelectCourse(bizTypeId,sId.toString(), cookie)

                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            selectCourseData.value = response.body()?.string()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
        }
    }

    val selectCourseInfoData = StateHolder<List<SelectCourseInfo>>()
    suspend fun getSelectCourseInfo(cookie: String, id : Int) = launchRequestSimple(
        holder = selectCourseInfoData,
        request = { jxglstuJSON.getSelectCourseInfo(id,cookie).awaitResponse() },
        transformSuccess = { _,json -> parseSelectCourseInfo(json) }
    )
    private fun parseSelectCourseInfo(json : String) : List<SelectCourseInfo> = try {
        val courses: List<SelectCourseInfo> = Gson().fromJson(json, object : TypeToken<List<SelectCourseInfo>>() {}.type)
        courses
    } catch (e : Exception) { throw e }

    val stdCountData = MutableLiveData<String?>()
    fun getSCount(cookie: String,id : Int) {
        val call = jxglstuJSON.getCount(id,cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                stdCountData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    val requestIdData = MutableLiveData<String?>()
    suspend fun getRequestID(cookie: String, lessonId : String, courseId : String, type : String) {
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val call = jxglstuJSON.getRequestID(sId.toString(),lessonId,courseId,cookie,type)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    requestIdData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    val selectedData = MutableLiveData<String?>()
    suspend fun getSelectedCourse(cookie: String, courseId : String) {
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val call = jxglstuJSON.getSelectedCourse(sId.toString(),courseId,cookie)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    selectedData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }

    }

    val selectResultData = MutableLiveData<String?>()
    suspend fun postSelect(cookie: String,requestId : String) {
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val call = jxglstuJSON.postSelect(sId.toString(), requestId,cookie)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    selectResultData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

// 转专业 ////////////////////////////////////////////////////////////////////////////////////////////////
    val transferData = StateHolder<TransferResponse>()
    suspend fun getTransfer(cookie: String,batchId: String) = onListenStateHolderForNetwork(studentId,transferData) { sId ->
        launchRequestSimple(
            holder = transferData,
            request = { jxglstuJSON.getTransfer(cookie, batchId, sId).awaitResponse() },
            transformSuccess = { _, json -> parseTransfer(json) }
        )
    }
    private fun parseTransfer(json : String) : TransferResponse = try {
        Gson().fromJson(json, TransferResponse::class.java)
    } catch (e : Exception) { throw e }

    val transferListData = StateHolder<List<ChangeMajorInfo>>()
    suspend fun getTransferList(cookie: String) = onListenStateHolderForNetwork(studentId,transferListData) { sId ->
        launchRequestSimple(
            holder = transferListData,
            request = { jxglstuHTML.getTransferList(cookie, sId).awaitResponse() },
            transformSuccess = { _, html -> parseTransferList(html) }
        )
    }
    private fun parseTransferList(html : String) : List<ChangeMajorInfo> = try {
        val document = Jsoup.parse(html)
        val result = mutableListOf<ChangeMajorInfo>()

        // 获取所有的 turn-panel 元素
        val turnPanels = document.select(".turn-panel")
        for (panel in turnPanels) {
            val title = panel.select(".turn-title span").text()
            val dataValue = panel.select(".change-major-enter").attr("data")
            val applicationDate = panel.select(".open-date .text-primary").text()
            val admissionDate = panel.select(".select-date .text-warning").text()

            if (title.isNotBlank() && dataValue.isNotBlank()) {
                result.add(
                    ChangeMajorInfo(
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


    val myApplyData = StateHolder<MyApplyResponse>()
    suspend fun getMyApply(cookie: String,batchId: String) = onListenStateHolderForNetwork(studentId,myApplyData) { sId ->
        launchRequestSimple(
            holder = myApplyData,
            request = { jxglstuJSON.getMyTransfer(cookie, batchId, sId).awaitResponse() },
            transformSuccess = { _, json -> parseMyApply(json) }
        )
    }
    private fun parseMyApply(json: String) : MyApplyResponse = try {
        Gson().fromJson(json, MyApplyResponse::class.java)
    } catch (e : Exception) { throw e }

    val myApplyInfoData = StateHolder<MyApplyInfoBean>()
    suspend fun getMyApplyInfo(cookie: String, listId: Int) = onListenStateHolderForNetwork(studentId,myApplyInfoData) { sId ->
        launchRequestSimple(
            holder = myApplyInfoData,
            request = { jxglstuHTML.getMyTransferInfo(cookie, listId, sId).awaitResponse() },
            transformSuccess = { _, html -> parseMyApplyGradeInfo(html) }
        )
    }
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

// 新闻 ////////////////////////////////////////////////////////////////////////////////////////////////
    val newsResult = StateHolder<List<NewsResponse>>()
    suspend fun searchNews(title : String,page: Int = 1) = Repository.searchNews(title,page,newsResult)

    val newsXuanChengResult = StateHolder<List<NewsResponse>>()
    fun searchXuanChengNews(title : String, page: Int = 1) = Repository.searchXuanChengNews(title,page)
    suspend fun getXuanChengNews(page: Int) = Repository.getXuanChengNews(page,newsXuanChengResult)

    val academicResp = StateHolder<AcademicNewsResponse>()
    suspend fun getAcademicNews(type: AcademicType, page: Int = 1,totalPage : Int? = null) = Repository.getAcademic(type,totalPage,page,academicResp)

    val academicXCResp = StateHolder<List<NewsResponse>>()
    suspend fun getAcademicXCNews(type: AcademicXCType, page: Int = 1) = Repository.getAcademicXC(type,page,academicXCResp)
// 核心业务 ////////////////////////////////////////////////////////////////////////////////////////////////
    fun gotoCommunity(cookie : String) {

        val call = login.loginGoTo(service = LoginType.COMMUNITY.service,cookie = cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val loginCommunityData = MutableLiveData<String?>()
    fun loginCommunity(ticket : String) {

        val call = community.Login(ticket)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                loginCommunityData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val jxglstuGradeData = StateHolder<List<GradeJxglstuDTO>>()
    suspend fun getGradeFromJxglstu(cookie: String, semester: Int?) = onListenStateHolderForNetwork(studentId,jxglstuGradeData) { sId ->
        launchRequestSimple(
            holder = jxglstuGradeData,
            request = { jxglstuHTML.getGrade(cookie, sId.toString(), semester).awaitResponse() },
            transformSuccess = { _, html -> parseJxglstuGrade(html) }
        )
    }
    private fun parseJxglstuGrade(html: String): List<GradeJxglstuDTO> = try {
        val doc = Jsoup.parse(html)
        val termElements = doc.select("h3")
        val tableElements = doc.select("table.student-grade-table")

        val result = mutableListOf<GradeJxglstuDTO>()

        for ((index, termElement) in termElements.withIndex()) {
            val term = termElement.text()
            val table = tableElements.getOrNull(index) ?: continue
            val rows = table.select("tr")
            val list = mutableListOf<GradeResponseJXGLSTU>()

            for(row in rows) {
                val tds = row.select("td") // 选择tr标签下的所有td标签
                if(!tds.isEmpty()) {
                    val titles = tds[0].text()
                    val codes = tds[2].text()
                    val scores =tds[3].text()
                    val gpa = tds[4].text()
                    val totalGrade = tds[5].text()
                    val grades = tds[6].text()
                    list.add(GradeResponseJXGLSTU(titles,scores,gpa,grades,totalGrade,codes))
                }
            }

            result.add(GradeJxglstuDTO(term, list))
        }

        result
    } catch (e: Exception) {
        throw e
    }


    fun jxglstuLogin(cookie : String) {

        val call = jxglstuJSON.jxglstulogin(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val bizTypeIdResponse = StateHolder<Int>()
    suspend fun getBizTypeId(cookie: String,studentId : Int) = launchRequestSimple(
        holder = bizTypeIdResponse,
        request = { jxglstuHTML.getBizTypeId(cookie, studentId).awaitResponse() },
        transformSuccess = { _, html -> parseBizTypeId(html) }
    )
    private fun parseBizTypeId(html : String): Int = try{
        CasInHFUT.getBizTypeId(html)!!
    } catch (e : Exception) {
        throw e
    }

    suspend fun getStudentId(cookie : String) = launchRequestSimple(
        holder = studentId,
        request = {
            Log.d("webVpnMode",webVpn.toString())
            jxglstuJSON.getStudentId(cookie).awaitResponse()
                  },
        transformRedirect = { headers -> parseStudentId(headers) },
        transformSuccess = { _,_ -> -1 }
    ) 
    suspend fun isValidStudentId() : Boolean {
        val state = studentId.state.first()
        when (state) {
            is UiState.Success -> {
                val data = state.data
                if(data > 0) {
                    return true
                }
            }
            else -> {}
        }
        return false
    }
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
            Log.e("cookieee", getKeyStackTrace(e))
            throw e
        }
    }

    //bizTypeId不是年级数！  //dataId为学生ID  //semesterId为学期Id，例如23-24第一学期为234
    suspend fun getLessonIds(cookie : String,studentId : Int,bizTypeId : Int) = launchRequestSimple(
        holder = lessonIds,
        request = {
            jxglstuJSON.getLessonIds(
                cookie,
                bizTypeId.toString(),
                SemseterParser.getSemseter().toString(),
                studentId.toString()
            ).awaitResponse()
        },
        transformSuccess = { _, json -> parseLessonIds(json) }
    )

    private fun parseLessonIds(json : String) : lessonResponse {
        saveString("courses",json)
        try {
            return Gson().fromJson(json, lessonResponse::class.java)
        } catch (e : Exception) { throw e }
    }

    val datumData = StateHolder<String>()
    suspend fun getDatum(cookie : String,lessonIdList : List<Int>) = onListenStateHolderForNetwork(studentId,datumData){ sId ->
        val lessonIdsArray = JsonArray()
        lessonIdList.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
        val jsonObject = JsonObject().apply {
            add("lessonIds", lessonIdsArray)//课程ID
            addProperty("studentId",sId)//学生ID
            addProperty("weekIndex", "")
        }
        launchRequestSimple(
            holder = datumData,
            request = { jxglstuJSON.getDatum(cookie, jsonObject).awaitResponse() },
            transformSuccess = { _, json -> parseDatum(json) }
        )
    }
    private fun parseDatum(json : String) : String {
        if (json.contains("result")) {
            saveString("json", json)
            try {
                return json
            } catch (e : Exception) {
                throw e
            }
        } else {
            throw Exception(json)
        }
    }

    suspend fun getInfo(cookie : String) {
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val call = jxglstuHTML.getInfo(cookie,sId.toString())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    saveString("info", response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
            val call2 = jxglstuHTML.getMyProfile(cookie)

            call2.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    saveString("profile", response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }


    val lessonTimesResponse = StateHolder<List<CourseUnitBean>>()
    suspend fun getLessonTimes(cookie: String,timeCampusId : Int) = launchRequestSimple(
        holder = lessonTimesResponse,
        request = { jxglstuJSON.getLessonTimes(cookie, JxglstuService.LessonTimeRequest(timeCampusId)).awaitResponse() },
        transformSuccess = { _,json -> parseLessonTimes(json) }
    )
    val lessonTimesResponseNext = StateHolder<List<CourseUnitBean>>()
    suspend fun getLessonTimesNext(cookie: String,timeCampusId : Int) = launchRequestSimple(
        holder = lessonTimesResponseNext,
        request = { jxglstuJSON.getLessonTimes(cookie, JxglstuService.LessonTimeRequest(timeCampusId)).awaitResponse() },
        transformSuccess = { _,json -> parseLessonTimes(json) }
    )
    private suspend fun parseLessonTimes(result: String) : List<CourseUnitBean> = withContext(Dispatchers.IO){
        DataStoreManager.saveCourseTable(result)
        return@withContext try {
            Gson().fromJson(result, LessonTimesResponse::class.java).result.courseUnitList
        } catch (e : Exception) {
            throw e
        }
    }

    val programData = StateHolder<ProgramResponse>()
    suspend fun getProgram(cookie: String) = onListenStateHolderForNetwork(studentId,programData) { sId ->
        launchRequestSimple(
            holder = programData,
            request = { jxglstuJSON.getProgram(cookie, sId.toString()).awaitResponse() },
            transformSuccess = { _, json -> parseProgram(json) }
        )
    }
    private fun parseProgram(result: String) : ProgramResponse {
        saveString("program", result)
        return try {
            Gson().fromJson(result,ProgramResponse::class.java)
        } catch (e : Exception) {
            throw e
        }
    }

    val programCompletionData = StateHolder<ProgramCompletionResponse>()
    suspend fun getProgramCompletion(cookie: String) = launchRequestSimple(
        holder = programCompletionData,
        request = { jxglstuJSON.getProgramCompletion(cookie).awaitResponse() },
        transformSuccess = { _,json -> parseProgramCompletion(json) }
    )
    private fun parseProgramCompletion(json : String) : ProgramCompletionResponse = try {
        val listType = object : TypeToken<List<ProgramCompletionResponse>>() {}.type
        val data : List<ProgramCompletionResponse> = Gson().fromJson(json, listType)
        data[0]
    } catch (e : Exception) { throw e }

    val programPerformanceData = StateHolder<ProgramBean>()
    suspend fun getProgramPerformance(cookie: String) = onListenStateHolderForNetwork(studentId,programPerformanceData) { sId ->
        launchRequestSimple(
            holder = programPerformanceData,
            request = { jxglstuJSON.getProgramPerformance(cookie, sId).awaitResponse() },
            transformSuccess = { _, json -> parseProgramPerformance(json) }
        )
    }
    private fun parseProgramPerformance(json : String) : ProgramBean = try {
        Gson().fromJson(json,ProgramBean::class.java)
    } catch (e : Exception) { throw e }

    val teacherSearchData = StateHolder<TeacherResponse>()
    suspend fun searchTeacher(name: String = "", direction: String = "") = Repository.searchTeacher(name = name,direction = direction,teacherSearchData)

    val courseSearchResponse = StateHolder<List<lessons>>()
    suspend fun searchCourse(
        cookie: String,
        className : String?,
        courseName : String?,
        semester : Int,
        courseId : String?
    ) = onListenStateHolderForNetwork(studentId,courseSearchResponse) { sId ->
        launchRequestSimple(
            holder = courseSearchResponse,
            request = {
                jxglstuJSON.searchCourse(
                    cookie,
                    sId.toString(),
                    semester,
                    className,
                    "1,${
                        prefs.getString(
                            "CourseSearchRequest",
                            MyApplication.PAGE_SIZE.toString()
                        ) ?: MyApplication.PAGE_SIZE
                    }",
                    courseName,
                    courseId
                ).awaitResponse()
            },
            transformSuccess = { _, json -> parseSearchCourse(json) }
        )
    }
    private fun parseSearchCourse(result : String) : List<lessons> = try {
        Gson().fromJson(result,CourseSearchResponse::class.java).data.map { it.lesson }
    } catch (e : Exception) { throw e }

    fun parseDatumCourse(result: String) : List<lessons> = try {
        Gson().fromJson(result,lessonResponse::class.java).lessons
    } catch (e : Exception) {
        emptyList<lessons>()
    }

    val surveyListData = StateHolder<List<forStdLessonSurveySearchVms>>()
    suspend fun getSurveyList(cookie: String, semester : Int) = onListenStateHolderForNetwork(studentId,surveyListData) { sId ->
        launchRequestSimple(
            holder = surveyListData,
            request = {
                jxglstuJSON.getSurveyList(cookie, sId.toString(), semester).awaitResponse()
            },
            transformSuccess = { _, json -> parseSurveyList(json) }
        )
    }
    private fun parseSurveyList(json : String) : List<forStdLessonSurveySearchVms> = try {
            Gson().fromJson(json, SurveyTeacherResponse::class.java).forStdLessonSurveySearchVms
    } catch (e : Exception) { throw e }

    val surveyData = StateHolder<SurveyResponse>()
    suspend fun getSurvey(cookie: String, id : String) = launchRequestSimple(
        holder = surveyData,
        request = { jxglstuJSON.getSurveyInfo(cookie,id).awaitResponse() },
        transformSuccess = { _,json -> parseSurvey(json) }
    )
    private fun parseSurvey(json : String) : SurveyResponse = try {
        Gson().fromJson(json, SurveyResponse::class.java)
    } catch (e : Exception) { throw e }

    val surveyToken = StateHolder<String>()
    suspend fun getSurveyToken(cookie: String, id : String) = onListenStateHolderForNetwork(studentId,surveyToken) { sId ->
        launchRequestSimple(
            holder = surveyToken,
            request = {
                jxglstuJSON.getSurveyToken(
                    cookie,
                    id,
                    "/for-std/lesson-survey/semester-index/${sId}"
                ).awaitResponse()
            },
            transformSuccess = { headers, _ -> parseSurveyToken(headers) }
        )
    }
    private fun parseSurveyToken(headers : Headers) : String = try {
        headers.toString().substringAfter("Set-Cookie:").substringBefore(";")
    } catch(e : Exception) { throw e }

    val surveyPostData = MutableLiveData<String?>()
    fun postSurvey(cookie : String,json: JsonObject){
        val call = jxglstuJSON.postSurvey(cookie,json)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                surveyPostData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    suspend fun getPhoto(cookie : String){
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val call = jxglstuJSON.getPhoto(cookie,sId.toString())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    //保存图片
                    // 将响应体转换为字节数组
                    try {
                        val bytes = response.body()?.bytes()
                        // 将字节数组转换为Base64编码的字符串
                        val base64String = Base64.encodeToString(bytes, Base64.DEFAULT)
                        // 保存编码后的字符串
                        saveString("photo",base64String)
                    } catch (_ : Exception) { }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }


    val courseBookResponse = StateHolder<Map<Long,CourseBookBean>>()
    suspend fun getCourseBook(cookie: String,semester: Int) = onListenStateHolderForNetwork(studentId,courseBookResponse) { sId ->
        onListenStateHolderForNetwork(bizTypeIdResponse,courseBookResponse) { bizTypeId ->
            launchRequestSimple(
                holder = courseBookResponse,
                request = { jxglstuJSON.getCourseBook(cookie, bizTypeId = bizTypeId, semesterId = semester, studentId = sId).awaitResponse() },
                transformSuccess = { _, json -> parseCourseBookNetwork(json) }
            )
        }
    }
    private suspend fun parseCourseBookNetwork(json : String) : Map<Long,CourseBookBean> = try {
        val gson = Gson()
        val data = gson.fromJson(json, CourseBookResponse::class.java).textbookAssignMap
        // 将JSON以String只保存data部分
        val dataJson = gson.toJson(data)
        DataStoreManager.saveCourseBook(dataJson)

        parseCourseBook(json)
    } catch (e : Exception) { throw e }

    fun parseCourseBook(json: String) : Map<Long,CourseBookBean> = try {
        val type = object : TypeToken<Map<String, CourseBookBean>>() {}.type
        val data: Map<String, CourseBookBean> = Gson().fromJson(json, type)
        // 键为id，与课程汇总对接
        // 将键转换为Long
        data.mapNotNull { (key, value) ->
            key.toLongOrNull()?.let { longKey ->
                longKey to value
            }
        }.toMap()
    } catch (e : Exception) { emptyMap() }

    fun goToOne(cookie : String)  {// 创建一个Call对象，用于发送异步请求

        val call = oneGoto.OneGoto(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun goToHuiXin(cookie : String)  {

        val call = oneGoto.OneGotoCard(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val huiXinBillResult = StateHolder<BillBean>()
    suspend fun getCardBill(
        auth : String,
        page : Int,
        size : Int =
            prefs.getString("CardRequest", MyApplication.PAGE_SIZE.toString())?.toIntOrNull()
                ?: MyApplication.PAGE_SIZE
    ) = launchRequestSimple(
        holder = huiXinBillResult,
        request = { huiXin.Cardget(auth,page,size.toString() ).awaitResponse() },
        transformSuccess = { _,json -> parseHuiXinBills(json) }
    )
    private fun parseHuiXinBills(json : String) : BillBean = try {
        if(json.contains("操作成功")){
            Gson().fromJson(json, BillResponse::class.java).data
        } else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    val huiXinCardInfoResponse = MutableLiveData<String?>()
    fun getHuiXinCardInfo(auth : String) {
        val call = huiXin.getYue(auth)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                huiXinCardInfoResponse.value = body
                saveString("cardyue",body )
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val cardPredictedResponse = StateHolder<ForecastAllBean>()
    suspend fun getCardPredicted(auth: String) = withContext(Dispatchers.IO) {
        suspend fun reloadAllBills(origin: BillBean) {
            huiXinBillResult.clear()
            getCardBill(auth, page = 1, size = origin.total)

            val newState = huiXinBillResult.state.first()
            if (newState is UiState.Success) {
                Repository.getCardPredicted(newState.data, cardPredictedResponse)
            }
        }

        val currentState = huiXinBillResult.state.first()

        when (currentState) {
            is UiState.Success -> {
                val data = currentState.data
                if(data.size != data.total) {
                    reloadAllBills(data)
                }
            }
            else -> {
                // 第一次加载，拉取一条记录获取总数
                getCardBill(auth, page = 1, size = 1)
                val stateAfterInit = huiXinBillResult.state.first()
                if (stateAfterInit is UiState.Success) {
                    reloadAllBills(stateAfterInit.data)
                }
            }
        }
    }

    val infoValue = MutableLiveData<String?>()
    val electricData = MutableLiveData<String?>()
    val showerData = MutableLiveData<String?>()
    fun getFee(auth: String,type : FeeType,room : String? = null,phoneNumber : String? = null) {

        val feeItemId = type.code.toString()
        val levels = when(type) {
            NET_XUANCHENG -> "0"
            ELECTRIC_XUANCHENG -> null
            SHOWER_XUANCHENG -> "1"
            FeeType.SHOWER_HEFEI -> "未适配"
            FeeType.WASHING_HEFEI -> "未适配"
            FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> "未适配"
            FeeType.ELECTRIC_HEFEI_GRADUATE -> "未适配"
        }
        val rooms = when(type) {
            NET_XUANCHENG -> null
            ELECTRIC_XUANCHENG -> room
            SHOWER_XUANCHENG -> null
            FeeType.SHOWER_HEFEI -> null
            FeeType.WASHING_HEFEI -> "未适配"
            FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> "未适配"
            FeeType.ELECTRIC_HEFEI_GRADUATE -> "未适配"
        }
        val phoneNumbers = when(type) {
            NET_XUANCHENG -> null
            ELECTRIC_XUANCHENG -> null
            SHOWER_XUANCHENG -> phoneNumber
            FeeType.SHOWER_HEFEI -> phoneNumber
            FeeType.WASHING_HEFEI -> "未适配"
            FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> "未适配"
            FeeType.ELECTRIC_HEFEI_GRADUATE -> "未适配"
        }
        val call = huiXin.getFee(auth, typeId = feeItemId, room = rooms, level = levels, phoneNumber = phoneNumbers)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                when(type) {
                    NET_XUANCHENG -> infoValue.value = responseBody
                    ELECTRIC_XUANCHENG ->  electricData.value = responseBody
                    SHOWER_XUANCHENG -> showerData.value = responseBody
                    else -> { showToast("未适配") }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val guaGuaUserInfo = MutableLiveData<String?>()
    fun getGuaGuaUserInfo() {
        val loginCode = prefs.getString("loginCode","") ?: ""
        val phoneNumber = prefs.getString("PHONENUM","") ?: ""
        val call = phoneNumber.let { loginCode.let { it1 -> guaGua.getUserInfo(it, it1) } }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                guaGuaUserInfo.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val orderIdData = StateHolder<String>()
    suspend fun payStep1(auth: String,json: String,pay : Float,type: FeeType) = launchRequestSimple(
        holder = orderIdData,
        request = {
            huiXin.pay(
                auth = auth,
                pay = pay,
                flag = "choose",
                paystep = 0,
                json = json,
                typeId = type.code,
                isWX = null,
                orderid = null,
                password = null,
                paytype = null,
                paytypeid = null,
                cardId = null
            ).awaitResponse()
        },
        transformSuccess = { _,json -> parseHuiXinPayStep1(json) }
    )
    private fun parseHuiXinPayStep1(result : String) : String = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, PayStep1Response::class.java).data.orderid
        } else {
            throw Exception("Step1失败 终止支付")
        }
    } catch (e : Exception) { throw e }

    val uuIdData = StateHolder<Map<String, String>>()
    suspend fun payStep2(auth: String,orderId : String,type : FeeType) = launchRequestSimple(
        holder = uuIdData,
        request = {
            huiXin.pay(
                auth = auth,
                pay = null,
                flag = null,
                paystep = 2,
                json = null,
                typeId = 261,
                isWX = null,
                orderid = orderId,
                password = null,
                paytype = "CARDTSM",
                paytypeid = type.payTypeId,
                cardId = null
            ).awaitResponse()
        },
        transformSuccess = { _,json -> parseHuiXinPayStep2(json) }
    )
    private fun parseHuiXinPayStep2(result : String) : Map<String, String> = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, PayStep2Response::class.java).data.passwordMap
        } else {
            throw Exception("Step2失败 终止支付")
        }
    } catch (e : Exception) { throw e }

    val payResultData = StateHolder<String>()
    suspend fun payStep3(auth: String,orderId : String,password : String,uuid : String,type: FeeType) = launchRequestSimple(
        holder = payResultData,
        request = {
            huiXin.pay(
                auth = auth,
                pay = null,
                flag = null,
                paystep = 2,
                json = null,
                isWX = 0,
                orderid = orderId,
                password = password,
                paytype = "CARDTSM",
                paytypeid = type.payTypeId,
                cardId = uuid,
                typeId = null
            ).awaitResponse()
        },
        transformSuccess = { _,json -> parseHuiXinPayStep3(json)}
    )
    private fun parseHuiXinPayStep3(result : String) : String = try {
        if(result.contains("success")) {
            Gson().fromJson(result, PayStep3Response::class.java).msg
        } else {
            throw Exception("支付失败")
        }
    } catch (e : Exception) { throw e }

    val changeLimitResponse = StateHolder<String>()
    suspend fun changeLimit(auth: String,json: JsonObject) = launchRequestSimple(
        holder = changeLimitResponse,
        request = { huiXin.changeLimit(auth,json).awaitResponse() },
        transformSuccess = { _,json -> parseHuiXinChangeLimit(json) }
    )
    private fun parseHuiXinChangeLimit(json : String) : String = try {
        Gson().fromJson(json,ChangeLimitResponse::class.java).msg
    } catch (e : Exception) { throw e }

    var huiXinRangeResult = StateHolder<Float>()
    suspend fun searchDate(auth : String, timeFrom : String, timeTo : String) = launchRequestSimple(
        holder = huiXinRangeResult,
        request = { huiXin.searchDate(auth,timeFrom,timeTo).awaitResponse() },
        transformSuccess = { _, json -> parseHuiXinRange(json) }
    )
    private fun parseHuiXinRange(result : String) : Float = try {
        if(result.contains("操作成功")) {
            val data = Gson().fromJson(result, BillRangeResponse::class.java)
            data.data.expenses / 100
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    val huiXinSearchBillsResult = StateHolder<BillBean>()
    suspend fun searchBills(auth : String, info: String,page : Int) = launchRequestSimple(
        holder = huiXinSearchBillsResult,
        request = { huiXin.searchBills(auth,info,page, prefs.getString("CardRequest","30") ?: MyApplication.PAGE_SIZE.toString()).awaitResponse() },
        transformSuccess = { _, json -> parseHuiXinSearchBills(json) }
    )
    private fun parseHuiXinSearchBills(result : String) : BillBean = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result,BillResponse::class.java).data
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    val huiXinMonthBillResult = StateHolder<List<BillMonth>>()
    suspend fun getMonthBills(auth : String, dateStr: String) = launchRequestSimple(
        holder = huiXinMonthBillResult,
        request = { huiXin.getMonthYue(auth,dateStr).awaitResponse() },
        transformSuccess = { _, json -> parseHuiXinMonthBills(json) }
    )
    private fun parseHuiXinMonthBills(json : String) : List<BillMonth> = try {
        if(json.contains("操作成功")) {
            val data = Gson().fromJson(json, BillMonthResponse::class.java)
            val bill = data.data
            bill.map { (date,balance) -> BillMonth(date, balance) }
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw e }

    fun getToken()  {

        val codeHttp = prefs.getString("code", "")
        var code = codeHttp
        if (code != null) { code = code.substringAfter("=") }
        if (code != null) { code = code.substringBefore("]") }
        val http = codeHttp?.substringAfter("[")?.substringBefore("]")


        val call = http?.let { code?.let { it1 -> one.getToken(it, it1) } }

        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                try {
                    val data = Gson().fromJson(json, getTokenResponse::class.java)
                    if (data.msg == "success") {
                        token.value = data.data.access_token
                        saveString("bearer", data.data.access_token)
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }

    fun getBorrowBooks(token : String)  {

        val call = one.getBorrowBooks(token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                if (json.toString().contains("success")) {
                    try {
                        val data = Gson().fromJson(json, BorrowBooksResponse::class.java)
                        val borrow = data.data.toString()
                        saveString("borrow",borrow)
                    } catch (_ : Exception) {}
                } else saveString("borrow","未获取")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getSubBooks(token : String)  {

        val call = one.getSubBooks(token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                if (json.toString().contains("success")) {
                    try {
                        val data = Gson().fromJson(json, SubBooksResponse::class.java)
                        val sub = data.data.toString()
                        saveString("sub", sub)
                    } catch (_ : Exception) {}
                }
                else saveString("borrow","0")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun searchEmptyRoom(buildingCode : String, token : String)  {

        val call = one.searchEmptyRoom(buildingCode, "Bearer $token")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("emptyjson", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val mailData = StateHolder<MailResponse?>()
    suspend fun getMailURL(token : String)  = launchRequestSimple(
        holder = mailData,
        request = {
            val secret = Encrypt.generateRandomHexString()
            val email = getSchoolEmail() ?: ""
            val chipperText = Encrypt.encryptAesECB(email,secret)
            val cookie = "secret=$secret"
            one.getMailURL(chipperText, "Bearer $token",cookie).awaitResponse()
        },
        transformSuccess = { _,json -> parseMailUrl(json) }
    )
    private fun parseMailUrl(result: String) : MailResponse? = try {
        if(result.contains("success"))
            Gson().fromJson(result,MailResponse::class.java)
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    val payFeeResponse = StateHolder<PayData>()
    suspend fun getPay() = launchRequestSimple(
        holder = payFeeResponse,
        request = {  one.getPay(getPersonInfo().username).awaitResponse()  },
        transformSuccess = { _,json -> parsePayFee(json) }
    )
    private fun parsePayFee(result : String) : PayData = try {
        Gson().fromJson(result,PayResponse::class.java).data
    } catch (e : Exception) { throw e }

    val dormitoryResult = StateHolder<List<XuanquResponse>>()
    suspend fun searchDormitoryXuanCheng(code : String) = Repository.searchDormitoryXuanCheng(code,dormitoryResult)

    val failRateData = StateHolder<List<FailRateRecord>>()
    suspend fun searchFailRate(token : String, name: String, page : Int) = launchRequestSimple(
        holder = failRateData,
        request = { community.getFailRate(token,name,page.toString(), prefs.getString("FailRateRequest", MyApplication.PAGE_SIZE.toString()) ?: MyApplication.PAGE_SIZE.toString()).awaitResponse() },
        transformSuccess = { _,json -> parseFailRate(json) }
    )
    private fun parseFailRate(json : String) : List<FailRateRecord> = try {
        if(json.contains("操作成功")) {
            Gson().fromJson(json, FailRateResponse::class.java).result.records
        } else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    val examCodeFromCommunityResponse = MutableLiveData<Int>()
    fun getExamFromCommunity(token: String) {
        val call = token.let { community.getExam(it) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responses = response.body()?.string()
                saveString("Exam", responses)
                examCodeFromCommunityResponse.value = response.code()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    val jxglstuExamCode = MutableLiveData<Int>()
    suspend fun getExamJXGLSTU(cookie: String) {
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val call = jxglstuJSON.getExam(cookie,sId.toString())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val code = response.code()
                    jxglstuExamCode.value = code
                    if(code == 200) {
                        saveString("examJXGLSTU", response.body()?.string())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    val gradeFromCommunityResponse = StateHolder<GradeResult>()
    suspend fun getGrade(token: String, year : String, term : String) = launchRequestSimple(
        holder = gradeFromCommunityResponse,
        request = { community.getGrade(token,year,term).awaitResponse() },
        transformSuccess = { _,json -> parseGradeFromCommunity(json) }
    )
    private fun parseGradeFromCommunity(json : String) : GradeResult = try {
        if(json.contains("success"))
            Gson().fromJson(json,GradeResponse::class.java).result
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    val avgData = StateHolder<AvgResult>()
    suspend fun getAvgGrade(token: String) = launchRequestSimple(
        holder = avgData,
        request = { community.getAvgGrade(token).awaitResponse() },
        transformSuccess = { _,json -> parseAvgGradeFromCommunity(json) }
    )
    private fun parseAvgGradeFromCommunity(result : String) : AvgResult = try {
        if(result.contains("success"))
            Gson().fromJson(result,GradeAvgResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    val allAvgData = StateHolder<List<GradeAllResult>>()
    suspend fun getAllAvgGrade(token: String) = launchRequestSimple(
        holder = allAvgData,
        request = { community.getAllAvgGrade(token).awaitResponse() },
        transformSuccess = { _,json -> parseAllAvgGradeFromCommunity(json) }
    )
    private fun parseAllAvgGradeFromCommunity(result : String) : List<GradeAllResult> = try {
        if(result.contains("success"))
            Gson().fromJson(result,GradeAllResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    val libraryData = StateHolder<List<LibRecord>>()
    suspend fun searchBooks(token: String, name: String, page: Int) = launchRequestSimple(
        holder = libraryData,
        request = { community.searchBooks(token,name,page.toString(),prefs.getString("BookRequest", MyApplication.PAGE_SIZE.toString())?: MyApplication.PAGE_SIZE.toString()).awaitResponse() },
        transformSuccess = { _,json -> parseSearchBooks(json) }
    )
    private fun parseSearchBooks(json : String) : List<LibRecord> = try {
        if(json.contains("操作成功"))
            Gson().fromJson(json, LibraryResponse::class.java).result.records
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    val bookPositionData = StateHolder<List<BookPositionBean>>()
    suspend fun getBookPosition(token: String,callNo: String) = launchRequestSimple(
        holder = bookPositionData,
        request = { community.getBookPosition(token,callNo).awaitResponse() },
        transformSuccess = { _,json -> parseBookPosition(json) }
    )
    private fun parseBookPosition(json : String) : List<BookPositionBean> = try {
        if(json.contains("成功"))
            Gson().fromJson(json,BookPositionResponse::class.java).result
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    fun getCoursesFromCommunity(token : String, studentId: String? = null) {

        val call = token.let { community.getCourse(it,studentId) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(studentId == null)
                    saveString("Course", response.body()?.string())
                else
                    saveString("Course${studentId}", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun openFriend(token : String) {

        val call = token.let { community.switchShare(it, CommunityService.RequestJson(1)) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val dormitoryFromCommunityResp = StateHolder<DormitoryBean>()
    suspend fun getDormitory(token : String) = launchRequestSimple(
        holder = dormitoryFromCommunityResp,
        request = { community.getDormitory(token).awaitResponse() },
        transformSuccess = { _,json -> parseDormitory(json) }
    )
    private fun parseDormitory(result : String) : DormitoryBean = try {
        if (result.contains("操作成功")) {
            val list = Gson().fromJson(result, DormitoryResponse::class.java).result
            if(list.isEmpty()) throw Exception("无住宿信息") else list[0]
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    val dormitoryInfoFromCommunityResp = StateHolder<List<DormitoryUser>>()
    suspend fun getDormitoryInfo(token : String) = onListenStateHolderForNetwork(dormitoryFromCommunityResp,dormitoryInfoFromCommunityResp) { d ->
        launchRequestSimple(
            holder = dormitoryInfoFromCommunityResp,
            request = { community.getDormitoryInfo(token,d.campus,d.room,d.dormitory).awaitResponse() },
            transformSuccess = { _,json -> parseDormitoryInfo(json) }
        )
    }
    private fun parseDormitoryInfo(result : String) : List<DormitoryUser> = try {
        if (result.contains("操作成功")) {
            val list1 = Gson().fromJson(result, DormitoryInfoResponse::class.java).result.profileList
            list1.flatMap { it.userList }
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    val addFriendApplyResponse = StateHolder<String>()
    suspend fun addFriendApply(token : String, username : String) = launchRequestSimple(
        holder = addFriendApplyResponse,
        request = { community.applyAdd(token,CommunityService.RequestJsonApply(username)).awaitResponse() },
        transformSuccess = { _,json -> parseApplyFriend(json) }
    )
    private fun parseApplyFriend(result : String) : String = try {
        if (result.contains("success"))
            Gson().fromJson(result,ApplyFriendResponse::class.java).message
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    val applyFriendsResponse = StateHolder<List<ApplyingLists?>>()
    suspend fun getApplying(token : String) = launchRequestSimple(
        holder = applyFriendsResponse,
        request = { community.getApplyingList(token, prefs.getString("FriendRequest", MyApplication.PAGE_SIZE.toString())?: MyApplication.PAGE_SIZE.toString()).awaitResponse() },
        transformSuccess = { _,json -> parseApplyFriends(json) }
    )
    private fun parseApplyFriends(result : String) : List<ApplyingLists?> = try {
        if(result.contains("success"))
            Gson().fromJson(result,ApplyingResponse::class.java).result.records
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    val mapsResponse = StateHolder<List<MapBean>>()
    suspend fun getMaps(token : String) = launchRequestSimple(
        holder = mapsResponse,
        request = { community.getCampusMap(token).awaitResponse() },
        transformSuccess = { _,json -> parseMaps(json) }
    )
    private fun parseMaps(result : String) : List<MapBean> = try {
        if(result.contains("操作成功"))
            Gson().fromJson(result, MapResponse::class.java).result
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    val stuAppsResponse = StateHolder<List<StuAppBean>>()
    suspend fun getStuApps(token : String) = launchRequestSimple(
        holder = stuAppsResponse,
        request = { community.getStuApps(token).awaitResponse() },
        transformSuccess = { _,json -> parseStuApps(json) }
    )
    private fun parseStuApps(result : String) : List<StuAppBean> = try {
        if(result.contains("操作成功")) {
            val list = Gson().fromJson(result, StuAppsResponse::class.java).result
            val totalList = list.flatMap { it.subList }
            totalList.filter { it.url?.startsWith(MyApplication.STU_URL) == true }
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }


    val booksChipData = StateHolder<List<BorrowRecords>>()
    suspend fun communityBooks(token : String,type : LibraryItems,page : Int = 1) = launchRequestSimple(
        holder = booksChipData,
        request = {
            val size = 500
            when(type) {
                LibraryItems.OVERDUE -> community.getOverDueBook(token, page.toString(),size.toString())
                LibraryItems.HISTORY -> community.getHistoryBook(token, page.toString(),size.toString())
                LibraryItems.BORROWED -> community.getBorrowedBook(token, page.toString(),size.toString())
            }.awaitResponse()
        },
        transformSuccess = { _,json -> parseMyBookFromCommunity(json) }
    )
    private fun parseMyBookFromCommunity(json : String) : List<BorrowRecords> = try {
        if(json.contains("success"))
            Gson().fromJson(json,BorrowResponse::class.java).result.records
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    val todayFormCommunityResponse = StateHolder<TodayResult>()
    suspend fun getToday(token : String) = launchRequestSimple(
        holder = todayFormCommunityResponse,
        request = { community.getToday(token).awaitResponse() },
        transformSuccess = { _,json -> parseTodayFromCommunity(json) }
    )
    private fun parseTodayFromCommunity(result : String) : TodayResult = try {
        Gson().fromJson(result,TodayResponse::class.java).result
    } catch (e : Exception) { throw e }


    fun getFriends(token : String) {

        val call = token.let { community.getFriends(it) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("feiends", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun checkApplying(token : String, id : String, isOk : Boolean) {
        val call = token.let { community.checkApplying(it,CommunityService.RequestApplyingJson(id,if(isOk) 1 else 0)) }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val lessonIdsNext = StateHolder<lessonResponse>()
    suspend fun getLessonIdsNext(cookie : String, studentId : Int, bizTypeId: Int) = launchRequestSimple(
        holder = lessonIdsNext,
        request = { (SemseterParser.getSemseter().plus(20)).toString().let { jxglstuJSON.getLessonIds(cookie,bizTypeId.toString(), it,studentId.toString()).awaitResponse() }},
        transformSuccess = { _,json -> parseLessonIdsNext(json) }
    )
    private fun parseLessonIdsNext(json : String) : lessonResponse {
        saveString("coursesNext",json)
        try {
            return Gson().fromJson(json, lessonResponse::class.java)
        } catch (e : Exception) { throw e }
    }
//            {
//        val call = (SemseterParser.getSemseter().plus(20)).toString().let { jxglstuJSON.getLessonIds(cookie,bizTypeId.toString(), it,studentId.toString()) }
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                val json = response.body()?.string()
//                if (json != null) {
//                    try {
//                        val id = Gson().fromJson(json, lessonResponse::class.java)
//                        saveString("coursesNext",json)
//                        lessonIdsNext.value = id.lessonIds
//                    } catch (_ : Exception) {}
//                }
//            }
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
//        })
//    }

    suspend fun getDatumNext(cookie : String, lessonIdList: List<Int>) {
//        val lessonIdsArray = JsonArray()
//        this@NetWorkViewModel.lessonIds.value?.forEach {lessonIdsArray.add(JsonPrimitive(it))}
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val lessonIdsArray = JsonArray()
            lessonIdList.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
            val jsonObject = JsonObject().apply {
                add("lessonIds", lessonIdsArray)//课程ID
                addProperty("studentId",sId)//学生ID
                addProperty("weekIndex", "")
            }
            val call = jxglstuJSON.getDatum(cookie,jsonObject)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val body = response.body()?.string()
                    saveString("jsonNext", body)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }

    }

    val weatherWarningData = StateHolder<List<QWeatherWarnBean>>()
    suspend fun getWeatherWarn(campus: Campus) = Repository.getWeatherWarn(campus,weatherWarningData)
    val qWeatherResult = StateHolder<QWeatherNowBean>()
    suspend fun getWeather(campus: Campus) = Repository.getWeather(campus,qWeatherResult)
// 学工系统/今日校园 ////////////////////////////////////////////////////////////////////////////////////////////////

    val goToStuResponse = MutableLiveData<String?>()
    val stuTicket = MutableLiveData<String?>(null)
    fun loginToStu(cookie : String) {
        val call = login.loginGoTo(service = LoginType.STU.service, cookie = cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val statusCode = response.code()
                if(statusCode == 302) {
                    goToStuResponse.value = response.headers()["Location"]
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val loginStuResponse = MutableLiveData<String?>()
    fun loginRefreshStu(ticket : String?,cookie: String?) {
        val call = stu.login(cookie,ticket)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    loginStuResponse.value = response.headers()["Set-Cookie"]
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val stuInfoResponse = MutableLiveData<String?>()
    fun getStuInfo(cookie: String) = Repository.makeRequest(
        call = stu.getStudentInfo(cookie),
        liveData = stuInfoResponse
    )
// 宣城校园网 ////////////////////////////////////////////////////////////////////////////////////////////////

    val loginSchoolNetResponse = StateHolder<Boolean>()
    suspend fun loginSchoolNet(campus: Campus = getCampus()) = Repository.loginSchoolNet(campus,loginSchoolNetResponse)
    suspend fun logoutSchoolNet(campus: Campus = getCampus()) = Repository.logoutSchoolNet(campus,loginSchoolNetResponse)

    val infoWebValue = StateHolder<WebInfo>()
    suspend fun getWebInfo() = Repository.getWebInfo(infoWebValue)
    suspend fun getWebInfo2() = Repository.getWebInfo2(infoWebValue)

    fun getUpdate() = Repository.getUpdate()
}


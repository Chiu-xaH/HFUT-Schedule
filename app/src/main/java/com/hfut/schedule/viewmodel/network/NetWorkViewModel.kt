package com.hfut.schedule.viewmodel.network

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.AdmissionType
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.CampusRegion.HEFEI
import com.hfut.schedule.logic.enumeration.CampusRegion.XUANCHENG
import com.hfut.schedule.logic.enumeration.LibraryItems
import com.hfut.schedule.logic.enumeration.LoginType
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.AcademicNewsResponse
import com.hfut.schedule.logic.model.AcademicType
import com.hfut.schedule.logic.model.AcademicXCType
import com.hfut.schedule.logic.model.AdmissionDetailBean
import com.hfut.schedule.logic.model.AdmissionMapBean
import com.hfut.schedule.logic.model.AdmissionTokenResponse
import com.hfut.schedule.logic.model.GiteeReleaseResponse
import com.hfut.schedule.logic.model.GithubFolderBean
import com.hfut.schedule.logic.model.HaiLeDeviceDetailBean
import com.hfut.schedule.logic.model.HaiLeDeviceDetailRequestBody
import com.hfut.schedule.logic.model.HaiLeNearPositionBean
import com.hfut.schedule.logic.model.HaiLeNearPositionRequestDTO
import com.hfut.schedule.logic.model.NewsResponse
import com.hfut.schedule.logic.model.OfficeHallSearchBean
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
import com.hfut.schedule.logic.model.community.BusBean
import com.hfut.schedule.logic.model.community.BusResponse
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
import com.hfut.schedule.logic.model.community.LoginCommunityResponse
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
import com.hfut.schedule.logic.model.jxglstu.SelectCourse
import com.hfut.schedule.logic.model.jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.model.jxglstu.SelectPostResponse
import com.hfut.schedule.logic.model.jxglstu.SurveyResponse
import com.hfut.schedule.logic.model.jxglstu.SurveyTeacherResponse
import com.hfut.schedule.logic.model.jxglstu.TransferPostResponse
import com.hfut.schedule.logic.model.jxglstu.TransferResponse
import com.hfut.schedule.logic.model.jxglstu.forStdLessonSurveySearchVms
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.model.one.BuildingBean
import com.hfut.schedule.logic.model.one.BuildingResponse
import com.hfut.schedule.logic.model.one.ClassroomBean
import com.hfut.schedule.logic.model.one.ClassroomResponse
import com.hfut.schedule.logic.model.one.getTokenResponse
import com.hfut.schedule.logic.model.wx.WXClassmatesBean
import com.hfut.schedule.logic.model.wx.WXPersonInfoBean
import com.hfut.schedule.logic.model.huixin.BillMonth
import com.hfut.schedule.logic.model.huixin.BillMonthResponse
import com.hfut.schedule.logic.model.huixin.BillRangeResponse
import com.hfut.schedule.logic.model.huixin.ChangeLimitResponse
import com.hfut.schedule.logic.model.huixin.FeeType
import com.hfut.schedule.logic.model.huixin.FeeType.ELECTRIC_XUANCHENG
import com.hfut.schedule.logic.model.huixin.FeeType.NET_XUANCHENG
import com.hfut.schedule.logic.model.huixin.FeeType.SHOWER_XUANCHENG
import com.hfut.schedule.logic.model.huixin.HuiXinLoginResponse
import com.hfut.schedule.logic.model.huixin.PayStep1Response
import com.hfut.schedule.logic.model.huixin.PayStep2Response
import com.hfut.schedule.logic.model.huixin.PayStep3Response
import com.hfut.schedule.logic.model.zhijian.ZhiJianCourseItem
import com.hfut.schedule.logic.model.zhijian.ZhiJianCourseItemDto
import com.hfut.schedule.logic.model.zhijian.ZhiJianCoursesResponse
import com.hfut.schedule.logic.network.StatusCode
import com.hfut.schedule.logic.network.api.CommunityService
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.api.HuiXinService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.OneService
import com.hfut.schedule.logic.network.api.StuService
import com.hfut.schedule.logic.network.api.SupabaseService
import com.hfut.schedule.logic.network.api.ZhiJianService
import com.hfut.schedule.logic.network.repo.GithubRepository
import com.hfut.schedule.logic.network.repo.LoginSchoolNetRepository
import com.hfut.schedule.logic.network.repo.NewsRepository
import com.hfut.schedule.logic.network.repo.QWeatherRepository
import com.hfut.schedule.logic.network.repo.Repository
import com.hfut.schedule.logic.network.repo.WxRepository
import com.hfut.schedule.logic.network.repo.launchRequestNone
import com.hfut.schedule.logic.network.repo.launchRequestSimple
import com.hfut.schedule.logic.network.repo.makeRequest
import com.hfut.schedule.logic.network.servicecreator.CommunityServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.servicecreator.HuiXinServiceCreator
import com.hfut.schedule.logic.network.servicecreator.JxglstuServiceCreator
import com.hfut.schedule.logic.network.servicecreator.login.LoginServiceCreator
import com.hfut.schedule.logic.network.servicecreator.MyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneGotoServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneServiceCreator
import com.hfut.schedule.logic.network.servicecreator.StuServiceCreator
import com.hfut.schedule.logic.network.servicecreator.SupabaseServiceCreator
import com.hfut.schedule.logic.network.servicecreator.ZhiJianServiceCreator
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.state.CasInHFUT
import com.hfut.schedule.logic.util.network.state.PARSE_ERROR_CODE
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
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.ChangeMajorInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.GradeAndRank
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.MyApplyInfoBean
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.PlaceAndTime
import com.hfut.schedule.ui.screen.home.search.function.one.mail.MailResponse
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.xah.bsdiffs.model.Patch
import com.xah.bsdiffs.util.parsePatch
import com.xah.shared.getConsumptionResult
import com.xah.shared.model.BillBean
import com.xah.shared.model.BillResponse
import com.xah.shared.model.TotalResult
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
import java.time.LocalDate

// 106个函数
class NetWorkViewModel() : ViewModel() {
    private fun createJSONService(): JxglstuService {
        return JxglstuServiceCreator.create(JxglstuService::class.java, GlobalUIStateHolder.webVpn)
    }

//    private fun createHTMLService(): JxglstuService {
//        return JxglstuHTMLServiceCreator.create(JxglstuService::class.java, GlobalUIStateHolder.webVpn)
//    }
    private var jxglstu = createJSONService()
//    private var jxglstuHTML = createHTMLService()
    private val oneGoto = OneGotoServiceCreator.create(LoginService::class.java)
    private val one = OneServiceCreator.create(OneService::class.java)
    private val huiXin = HuiXinServiceCreator.create(HuiXinService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)
    private val community = CommunityServiceCreator.create(CommunityService::class.java)
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
    private val zhiJian = ZhiJianServiceCreator.create(ZhiJianService::class.java)
    private val myAPI = MyServiceCreator.create(MyService::class.java)
    private val stu = StuServiceCreator.create(StuService::class.java)
    private val supabase = SupabaseServiceCreator.create(SupabaseService::class.java)
    fun updateServices() {
        jxglstu = createJSONService()
//        jxglstuHTML = createHTMLService()
    }
    val studentId = StateHolder<Int>()
    val lessonIds = StateHolder<lessonResponse>()
    val token = MutableLiveData<String>()

    suspend fun checkJxglstuCanUse() = launchRequestNone {
        jxglstu.checkCanUse().awaitResponse()
    }

    val wxLoginResponse = StateHolder<String>()
    suspend fun wxLogin() = WxRepository.wxLogin(wxLoginResponse)

    val wxPersonInfoResponse = StateHolder<WXPersonInfoBean>()
    suspend fun wxGetPersonInfo(auth : String) = WxRepository.wxGetPersonInfo(auth,wxPersonInfoResponse)

    val wxClassmatesResponse = StateHolder<WXClassmatesBean>()
    suspend fun wxGetClassmates(auth : String) = onListenStateHolderForNetwork(wxPersonInfoResponse,wxClassmatesResponse) { person ->
        WxRepository.wxGetClassmates(person.orgId,auth,wxClassmatesResponse)
    }

    val wxLoginCasResponse = StateHolder<Pair<String, Boolean>>()
    suspend fun wxLoginCas(auth : String,url : String) = WxRepository.wxLoginCas(url,auth,wxLoginCasResponse)

    val wxConfirmLoginResponse = StateHolder<String>()
    suspend fun wxConfirmLogin(auth : String,uuid : String) = WxRepository.wxConfirmLogin(uuid,auth,wxConfirmLoginResponse)

    val haiLeNearPositionResp = StateHolder<List<HaiLeNearPositionBean>>()
    suspend fun getHaiLeNearPosition(bean : HaiLeNearPositionRequestDTO) = Repository.getHaiLeNear(bean,haiLeNearPositionResp)

    val giteeApkSizeResp = StateHolder<Double>()
    suspend fun getGiteeApkSize(version : String) = GithubRepository.getUpdateFileSize("$version.apk",giteeApkSizeResp)
    val giteePatchSizeResp = StateHolder<Double>()
    suspend fun getGiteePatchSize(patch: Patch) = GithubRepository.getUpdateFileSize(parsePatch(patch),giteePatchSizeResp)

    val haiLeDeviceDetailResp = StateHolder<List<HaiLeDeviceDetailBean>>()
    suspend fun getHaiLeDeviceDetail(bean : HaiLeDeviceDetailRequestBody) = Repository.getHaiLDeviceDetail(bean,haiLeDeviceDetailResp)

    val githubStarsData = StateHolder<Int>()
    suspend fun getStarNum() = GithubRepository.getStarNum(githubStarsData)

    val githubFolderResp = StateHolder<List<GithubFolderBean>>()
    suspend fun getUpdateContents() = GithubRepository.getUpdateContents(githubFolderResp)


    val workSearchResult = StateHolder<WorkSearchResponse>()
    suspend fun searchWorks(keyword: String?, page: Int = 1,type: Int,campus: CampusRegion) = Repository.searchWorks(keyword,page,type,campus,workSearchResult)
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
    fun supabaseReg(password: String) = makeRequest(supabase.reg(user = SupabaseUserLoginBean(password = password)),supabaseRegResp)

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
    fun supabaseGetEvents() = makeRequest(supabase.getEvents(),supabaseGetEventsResp)

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
    suspend fun supabaseGetMyEvents() = launchRequestSimple(
        holder = supabaseGetMyEventsResp,// authorization = "Bearer $jwt"
        request = { supabase.getEvents(endTime = null,email = "eq." + getSchoolEmail()).awaitResponse() },
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
    suspend fun getProgramList(campus : CampusRegion) = launchRequestSimple(
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

    fun getMyApi() {
        val call = myAPI.my()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("my", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val programSearchData = StateHolder<ProgramSearchBean>()
    suspend fun getProgramListInfo(id : Int,campus : CampusRegion) = launchRequestSimple(
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

    fun downloadHoliday()  = GithubRepository.downloadHoliday()

    val postTransferResponse = StateHolder<String>()
    suspend fun postTransfer(cookie: String, batchId: String, id : String, phoneNumber : String) {
        onListenStateHolderForNetwork(studentId,postTransferResponse) { sId ->
            launchRequestSimple(
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
                jxglstu.getFormCookie(
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
                jxglstu.cancelTransfer(
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
    suspend fun verify(cookie: String) = launchRequestNone {
        jxglstu.verify(cookie).awaitResponse()
    }
    val selectCourseData = StateHolder<List<SelectCourse>>()
    suspend fun getSelectCourse(cookie: String) {
        onListenStateHolderForNetwork<Int, List<SelectCourse>>(studentId,selectCourseData) { sId ->
            onListenStateHolderForNetwork<Int,List<SelectCourse>>(bizTypeIdResponse,selectCourseData) { bizTypeId ->
                launchRequestSimple(
                    request = { jxglstu.getSelectCourse(bizTypeId,sId.toString(), cookie).awaitResponse() },
                    holder = selectCourseData,
                    transformSuccess = { _,json -> parseSelectedList(json) }
                )
            }
        }
    }
    private fun parseSelectedList(json : String) : List<SelectCourse> = try {
        val courses: List<SelectCourse> = Gson().fromJson(json, object : TypeToken<List<SelectCourse>>() {}.type)
        courses
    } catch (e : Exception) { throw e }

    val selectCourseInfoData = StateHolder<List<SelectCourseInfo>>()
    suspend fun getSelectCourseInfo(cookie: String, id : Int) = launchRequestSimple(
        holder = selectCourseInfoData,
        request = { jxglstu.getSelectCourseInfo(id,cookie).awaitResponse() },
        transformSuccess = { _,json -> parseSelectCourseInfo(json) }
    )
    private fun parseSelectCourseInfo(json : String) : List<SelectCourseInfo> = try {
        val courses: List<SelectCourseInfo> = Gson().fromJson(json, object : TypeToken<List<SelectCourseInfo>>() {}.type)
        courses
    } catch (e : Exception) { throw e }

    val stdCountData = MutableLiveData<String?>()
    fun getSCount(cookie: String,id : Int) {
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

    val requestIdData = StateHolder<String>()
    suspend fun getRequestID(cookie: String, lessonId : Int, courseId : Int, type : String) {
        onListenStateHolderForNetwork<Int,String>(studentId,requestIdData) { sId ->
            launchRequestSimple(
                request = { jxglstu.getRequestID(sId.toString(),lessonId.toString(),courseId.toString(),cookie,type).awaitResponse() },
                holder = requestIdData,
                transformSuccess = { _,body -> body }
            )
        }
    }

    val selectedData = StateHolder<List<SelectCourseInfo>>()
    suspend fun getSelectedCourse(cookie: String, courseId : Int) {
        onListenStateHolderForNetwork<Int,List<SelectCourseInfo>>(studentId,selectedData) { sId ->
            launchRequestSimple(
                request = { jxglstu.getSelectedCourse(sId.toString(),courseId.toString(),cookie).awaitResponse() },
                holder = selectedData,
                transformSuccess = { _,json -> parseSelectedCourses(json) }
            )
        }
    }
    private fun parseSelectedCourses(json : String) : List<SelectCourseInfo> = try {
        val courses: List<SelectCourseInfo> = Gson().fromJson(json, object : TypeToken<List<SelectCourseInfo>>() {}.type)
        courses
    } catch (e : Exception) { throw e }

    val selectResultData = StateHolder<Pair<Boolean, String>>()
    suspend fun postSelect(cookie: String,requestId : String) {
        onListenStateHolderForNetwork<Int, Pair<Boolean, String>>(studentId,selectResultData) { sId ->
            launchRequestSimple(
                holder = selectResultData,
                request = { jxglstu.postSelect(sId.toString(), requestId,cookie).awaitResponse() },
                transformSuccess = { _,json -> parseSelectResult(json) }
            )
        }
    }
    private fun parseSelectResult(json : String) : Pair<Boolean, String> = try {
        val data = Gson().fromJson(json, SelectPostResponse::class.java)
        val status = data.success
        val statusText = if(status) {
            "成功"
        } else {
            data.errorMessage?.textZh ?: "失败"
        }
        Pair(status,statusText)
    } catch (e : Exception) { throw e }

// 转专业 ////////////////////////////////////////////////////////////////////////////////////////////////
    val transferData = StateHolder<TransferResponse>()
    suspend fun getTransfer(cookie: String,batchId: String) = onListenStateHolderForNetwork(studentId,transferData) { sId ->
        launchRequestSimple(
            holder = transferData,
            request = { jxglstu.getTransfer(cookie, batchId, sId).awaitResponse() },
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
            request = { jxglstu.getTransferList(cookie, sId).awaitResponse() },
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
            request = { jxglstu.getMyTransfer(cookie, batchId, sId).awaitResponse() },
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
            request = { jxglstu.getMyTransferInfo(cookie, listId, sId).awaitResponse() },
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
    suspend fun searchNews(title : String,page: Int = 1) = NewsRepository.searchNews(title,page,newsResult)

    val newsXuanChengResult = StateHolder<List<NewsResponse>>()
    fun searchXuanChengNews(title : String, page: Int = 1) = NewsRepository.searchXuanChengNews(title,page)
    suspend fun getXuanChengNews(page: Int) = NewsRepository.getXuanChengNews(page,newsXuanChengResult)

    val academicResp = StateHolder<AcademicNewsResponse>()
    suspend fun getAcademicNews(type: AcademicType, page: Int = 1,totalPage : Int? = null) = NewsRepository.getAcademic(type,totalPage,page,academicResp)

    val academicXCResp = StateHolder<List<NewsResponse>>()
    suspend fun getAcademicXCNews(type: AcademicXCType, page: Int = 1) = NewsRepository.getAcademicXC(type,page,academicXCResp)
// 核心业务 ////////////////////////////////////////////////////////////////////////////////////////////////
    suspend fun gotoCommunity(cookie : String) = launchRequestNone {
        login.loginGoTo(service = LoginType.COMMUNITY.service,cookie = cookie).awaitResponse()
    }
    suspend fun gotoZhiJian(cookie : String) = launchRequestNone {
        login.loginGoTo(service = LoginType.ZHI_JIAN.service,cookie = cookie).awaitResponse()
    }
    suspend fun gotoLibrary(cookie : String) = launchRequestNone {
        login.loginGoTo(service = LoginType.LIBRARY.service,cookie = cookie).awaitResponse()
    }




    val checkStuLoginResp = StateHolder<Boolean>()
    suspend fun checkStuLogin(cookie : String) = launchRequestSimple(
        request = { stu.checkLogin(cookie).awaitResponse() },
        holder = checkStuLoginResp,
        transformSuccess = { _,json -> parseCheckStuLogin(json) }
    )
    private fun parseCheckStuLogin(json : String) = try {
        val sId = getPersonInfo().studentId ?: throw Exception("无学号")
        json.contains(sId)
    } catch (e : Exception) { throw e }


    val checkLibraryLoginResp = StateHolder<Boolean>()
    suspend fun checkLibraryLogin(token : String) = Repository.checkLibraryLogin(token,checkLibraryLoginResp)

    val loginCommunityData = StateHolder<String>()
    suspend fun loginCommunity(ticket : String) = launchRequestSimple(
        holder = loginCommunityData,
        request = { community.login(ticket).awaitResponse() },
        transformSuccess = { _,json -> parseCommunity(json) }
    )
    private fun parseCommunity(json : String) : String = try {
        if (json.contains(StatusCode.OK.code.toString())) {
            val token = Gson().fromJson(json, LoginCommunityResponse::class.java).result.token!!
            saveString("TOKEN", token)
            showToast("智慧社区登陆成功")
            token
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw e }


    private fun buildZhiJianJson(date: String, idNumber: String): String {
        val map = mapOf(
            "date" to date,
            "id_number" to idNumber
        )
        return Gson().toJson(map)
    }
    val zhiJianCourseResp = StateHolder<List<ZhiJianCourseItemDto>>()
    suspend fun getZhiJianCourses(studentId : String, mondayDate : String, token : String) = launchRequestSimple(
        holder = zhiJianCourseResp,
        request = { zhiJian.getCourses(token,buildZhiJianJson(mondayDate,studentId)).awaitResponse() },
        transformSuccess = { _,json -> parseZhiJianCourses(json, mondayDate) }
    )
    private fun parseZhiJianCourses(json : String,mondayDate : String) : List<ZhiJianCourseItemDto> = try {
        val gson = Gson()
        val root = gson.fromJson(json, ZhiJianCoursesResponse::class.java)
        val data = root.data

        // 提取 kbdata 字符串
        val rawStr = data.rawJsonString
        // 再把这个字符串解析成二维数组
        val listType = object : TypeToken<List<ZhiJianCourseItem>>() {}.type
        val rawData: List<ZhiJianCourseItem> = gson.fromJson(rawStr, listType)
        val monday = LocalDate.parse(mondayDate)
        val sunday = monday.plusDays(6)

        rawData.filter { item ->
            // mondayDate始终传周一YYYY-MM-DD
            // 过滤掉it.date大于周日的项目，即只允许mondayDate(周一)~本周日
            val d = LocalDate.parse(item.date)
            !d.isBefore(monday) && !d.isAfter(sunday)
        }.mapNotNull {
            it.toDto()
        }
    } catch (e : Exception) { throw e }

    val zhiJianCheckLoginResp = StateHolder<Boolean>()
    suspend fun zhiJianCheckLogin(token : String) = launchRequestSimple(
        holder = zhiJianCheckLoginResp,
        request = { zhiJian.checkLogin(token).awaitResponse() },
        transformSuccess = { _,json -> parseZhiJianCheckLogin(json) }
    )
    private fun parseZhiJianCheckLogin(json : String) : Boolean = try {
        json.contains(getPersonInfo().studentId!!) || json.contains(getPersonInfo().name!!)
    } catch (e : Exception) { throw e }


//    val loginZhiJianData = StateHolder<String>()
//    suspend fun loginZhiJian(ticket : String) = launchRequestSimple(
//        holder = loginZhiJianData,
//        request = { zhiJian.login(ticket).awaitResponse() },
//        transformRedirect = { headers -> parseZhiJianLogin(headers,null) },
//        transformSuccess = { headers,json -> parseZhiJianLogin(headers,json) }
//    )
//    private fun parseZhiJianLogin(headers: Headers,json : String?) : String = try {
//        val token = headers["Location"]?.substringAfter("jsessionid=")?.substringBefore("?")
//        if (token != null) {
//            saveString("ZhiJian", token)
//            showToast("指间工大登陆成功")
//            token
//        } else {
//            throw Exception(json)
//        }
//    } catch (e : Exception) { throw e }

    val jxglstuGradeData = StateHolder<List<GradeJxglstuDTO>>()
    suspend fun getGradeFromJxglstu(cookie: String, semester: Int?) = onListenStateHolderForNetwork(studentId,jxglstuGradeData) { sId ->
        launchRequestSimple(
            holder = jxglstuGradeData,
            request = { jxglstu.getGrade(cookie, sId.toString(), semester).awaitResponse() },
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

        val call = jxglstu.jxglstulogin(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val bizTypeIdResponse = StateHolder<Int>()
    suspend fun getBizTypeId(cookie: String,studentId : Int) = launchRequestSimple(
        holder = bizTypeIdResponse,
        request = { jxglstu.getBizTypeId(cookie, studentId).awaitResponse() },
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
//            Log.d("webVpnMode",webVpn.toString())
            jxglstu.getStudentId(cookie).awaitResponse()
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
            jxglstu.getLessonIds(
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
            request = { jxglstu.getDatum(cookie, jsonObject).awaitResponse() },
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
            val call = jxglstu.getInfo(cookie,sId.toString())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    saveString("info", response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
            val call2 = jxglstu.getMyProfile(cookie)

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
        request = { jxglstu.getLessonTimes(cookie, JxglstuService.LessonTimeRequest(timeCampusId)).awaitResponse() },
        transformSuccess = { _,json -> parseLessonTimes(json) }
    )

    val lessonTimesResponseNext = StateHolder<List<CourseUnitBean>>()
    suspend fun getLessonTimesNext(cookie: String,timeCampusId : Int) = launchRequestSimple(
        holder = lessonTimesResponseNext,
        request = { jxglstu.getLessonTimes(cookie, JxglstuService.LessonTimeRequest(timeCampusId)).awaitResponse() },
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
            request = { jxglstu.getProgram(cookie, sId.toString()).awaitResponse() },
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
        request = { jxglstu.getProgramCompletion(cookie).awaitResponse() },
        transformSuccess = { _,json -> parseProgramCompletion(json) }
    )
    private fun parseProgramCompletion(json : String) : ProgramCompletionResponse = try {
        saveString("PROGRAM_COMPETITION", json)
        val listType = object : TypeToken<List<ProgramCompletionResponse>>() {}.type
        val data : List<ProgramCompletionResponse> = Gson().fromJson(json, listType)
        data[0]
    } catch (e : Exception) { throw e }

    val programPerformanceData = StateHolder<ProgramBean>()
    suspend fun getProgramPerformance(cookie: String) = onListenStateHolderForNetwork(studentId,programPerformanceData) { sId ->
        launchRequestSimple(
            holder = programPerformanceData,
            request = { jxglstu.getProgramPerformance(cookie, sId).awaitResponse() },
            transformSuccess = { _, json -> parseProgramPerformance(json) }
        )
    }
    private fun parseProgramPerformance(json : String) : ProgramBean = try {
        saveString("PROGRAM_PERFORMANCE", json)
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
                jxglstu.searchCourse(
                    cookie,
                    sId.toString(),
                    semester,
                    className,
                    "1,${
                        prefs.getString(
                            "BookRequest",
                            MyApplication.DEFAULT_PAGE_SIZE.toString()
                        ) ?: MyApplication.DEFAULT_PAGE_SIZE
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
                jxglstu.getSurveyList(cookie, sId.toString(), semester).awaitResponse()
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
        request = { jxglstu.getSurveyInfo(cookie,id).awaitResponse() },
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
                jxglstu.getSurveyToken(
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
        val call = jxglstu.postSurvey(cookie,json)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                surveyPostData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    suspend fun getPhoto(cookie : String){
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val call = jxglstu.getPhoto(cookie,sId.toString())

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
                request = { jxglstu.getCourseBook(cookie, bizTypeId = bizTypeId, semesterId = semester, studentId = sId).awaitResponse() },
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

    suspend fun goToOne(cookie : String) = launchRequestNone {// 创建一个Call对象，用于发送异步请求
        oneGoto.loginGoToOauth("BsHfutEduPortal", MyApplication.ONE_URL + "home/index",cookie).awaitResponse()
    }

    suspend fun goToHuiXin(cookie : String) = launchRequestNone {
        oneGoto.loginGoToOauth("Hfut2023Ydfwpt", MyApplication.HUI_XIN_URL + "berserker-auth/cas/oauth2url?oauth2url=${MyApplication.HUI_XIN_URL}berserker-base/redirect",cookie).awaitResponse()
    }

    val huiXinBillResult = StateHolder<BillBean>()
    suspend fun getCardBill(
        auth : String,
        page : Int,
        size : Int =
            prefs.getString("BookRequest", MyApplication.DEFAULT_PAGE_SIZE.toString())?.toIntOrNull()
                ?: MyApplication.DEFAULT_PAGE_SIZE
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

    val huiXinCheckLoginResp = StateHolder<Boolean>()
    suspend fun checkHuiXinLogin(auth : String)= launchRequestSimple(
        holder = huiXinCheckLoginResp,
        request = { huiXin.checkLogin(auth).awaitResponse() },
        transformSuccess = { _,json -> parseCheckLHuiXinLogin(json) }
    )
    private fun parseCheckLHuiXinLogin(json : String) : Boolean = try {
        if(json.contains("操作成功")) {
            true
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw  e }


    val huiXinLoginResp = StateHolder<String>()
    suspend fun huiXinSingleLogin(studentId : String,password: String) {
        launchRequestSimple(
            holder = huiXinLoginResp,
            request = { huiXin.login(studentId = studentId, password = password).awaitResponse() },
            transformSuccess = { _,json -> parseHuiXinLogin(json) }
        )
    }
    private fun parseHuiXinLogin(json : String) : String = try {
        val token = Gson().fromJson(json, HuiXinLoginResponse::class.java).token
        saveString("auth",token)
        showToast("一卡通登陆成功")
        token
    } catch (e : Exception) {
        showToast("一卡通登陆失败 ${e.message}")
        throw  e
    }

    val cardPredictedResponse = StateHolder<TotalResult>()
    suspend fun getCardPredicted(auth: String) = withContext(Dispatchers.IO) {
        suspend fun reloadAllBills(origin: BillBean) {
            huiXinBillResult.clear()
            getCardBill(auth, page = 1, size = origin.total)

            val newState = huiXinBillResult.state.first()
            if (newState is UiState.Success) {
                try {
                    val data = getConsumptionResult(newState.data)
                    cardPredictedResponse.emitData(data)
                } catch (e : Exception) {
                    cardPredictedResponse.emitError(e, PARSE_ERROR_CODE)
                }
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
        request = { huiXin.searchBills(auth,info,page, prefs.getString("BookRequest","30") ?: MyApplication.DEFAULT_PAGE_SIZE.toString()).awaitResponse() },
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

    fun getToken(code : String)  {
        val call = one.getToken(code,code.substringAfter("code="))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                try {
                    val data = Gson().fromJson(json, getTokenResponse::class.java)
                    if (data.msg.contains("success")) {
                        token.value = data.data.access_token
                        saveString("bearer", "Bearer " + data.data.access_token)
                        showToast("信息门户(邮箱,教室)登陆成功")
                    }
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }


    val checkOneLoginResp = StateHolder<Boolean>()
    suspend fun checkOneLogin(token : String) = launchRequestSimple(
        holder = checkOneLoginResp,
        request = { one.checkLogin(token).awaitResponse() },
        transformSuccess = { _,json -> parseCheckOneLogin(json) }
    )
    private fun parseCheckOneLogin(json : String) : Boolean = try {
        if(json.contains("success")) {
            true
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw  e }

    val buildingsResponse = StateHolder<Pair<Campus,List<BuildingBean>>>()
    suspend fun getBuildings(campus : Campus, token : String)  = launchRequestSimple(
        holder = buildingsResponse,
        request = {
            val code = when(campus) {
                Campus.XC -> "03"
                Campus.FCH -> "02"
                Campus.TXL -> "01"
            }
            one.getBuildings(code, token).awaitResponse()
        },
        transformSuccess = { _,json -> parseBuildings(campus,json) }
    )
    private fun parseBuildings(campus: Campus, result: String) : Pair<Campus, List<BuildingBean>> = try {
        if(result.contains("success"))
            Pair(campus,Gson().fromJson(result, BuildingResponse::class.java).data)
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    val classroomResponse = StateHolder<List<ClassroomBean>>()
    suspend fun getClassroomInfo(code : String,token : String)  = launchRequestSimple(
        holder = classroomResponse,
        request = { one.getClassroomInfo(code, token).awaitResponse() },
        transformSuccess = { _,json -> parseClassroom(json) }
    )
    private fun parseClassroom(result: String) : List<ClassroomBean> = try {
        if(result.contains("success"))
            Gson().fromJson(result, ClassroomResponse::class.java).data.records
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    val mailData = StateHolder<MailResponse>()
    suspend fun getMailURL(token : String)  = launchRequestSimple(
        holder = mailData,
        request = {
            val secret = Encrypt.generateRandomHexString()
            val email = getSchoolEmail() ?: ""
            val chipperText = Encrypt.encryptAesECB(email,secret)
            val cookie = "secret=$secret"
            one.getMailURL(chipperText, token,cookie).awaitResponse()
        },
        transformSuccess = { _,json -> parseMailUrl(json) }
    )
    private fun parseMailUrl(result: String) : MailResponse = try {
        if(result.contains("success"))
            Gson().fromJson(result,MailResponse::class.java)
        else
            throw Exception(result)
    } catch (e: Exception) { throw e }

    val payFeeResponse = StateHolder<PayData>()
    suspend fun getPay() = launchRequestSimple(
        holder = payFeeResponse,
        request = {  one.getPay(getPersonInfo().studentId).awaitResponse()  },
        transformSuccess = { _,json -> parsePayFee(json) }
    )
    private fun parsePayFee(result : String) : PayData = try {
        Gson().fromJson(result,PayResponse::class.java).data ?: throw Exception("数据为空")
    } catch (e : Exception) { throw e }

    val dormitoryResult = StateHolder<List<XuanquResponse>>()
    suspend fun searchDormitoryXuanCheng(code : String) = Repository.searchDormitoryXuanCheng(code,dormitoryResult)

    val failRateData = StateHolder<List<FailRateRecord>>()
    suspend fun searchFailRate(token : String, name: String, page : Int) = launchRequestSimple(
        holder = failRateData,
        request = { community.getFailRate(token,name,page.toString(), prefs.getString("BookRequest", MyApplication.DEFAULT_PAGE_SIZE.toString()) ?: MyApplication.DEFAULT_PAGE_SIZE.toString()).awaitResponse() },
        transformSuccess = { _,json -> parseFailRate(json) }
    )
    private fun parseFailRate(json : String) : List<FailRateRecord> = try {
        if(json.contains("操作成功")) {
            Gson().fromJson(json, FailRateResponse::class.java).result.records
        } else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    val checkCommunityResponse = StateHolder<Boolean>()
    suspend fun checkCommunityLogin(token: String) = launchRequestSimple(
        holder = checkCommunityResponse,
        request = { community.getExam(token).awaitResponse() },
        transformSuccess = { _,_ -> true }
    )

    val jxglstuExamCode = MutableLiveData<Int>()
    suspend fun getExamJXGLSTU(cookie: String) {
        onListenStateHolderForNetwork<Int,Unit>(studentId,null) { sId ->
            val call = jxglstu.getExam(cookie,sId.toString())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val code = response.code()
                    jxglstuExamCode.value = code
                    if(code == StatusCode.OK.code) {
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
        request = { community.searchBooks(token,name,page.toString(),prefs.getString("BookRequest", MyApplication.DEFAULT_PAGE_SIZE.toString())?: MyApplication.DEFAULT_PAGE_SIZE.toString()).awaitResponse() },
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
        request = { community.getApplyingList(token, prefs.getString("BookRequest", MyApplication.DEFAULT_PAGE_SIZE.toString())?: MyApplication.DEFAULT_PAGE_SIZE.toString()).awaitResponse() },
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


    val officeHallSearchResponse = StateHolder<List<OfficeHallSearchBean>>()

    suspend fun officeHallSearch(text : String, page : Int) = Repository.officeHallSearch(text,page,officeHallSearchResponse)

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

    val busResponse = StateHolder<List<BusBean>>()
    suspend fun getBus(token : String) = launchRequestSimple(
        holder = busResponse,
        request = { community.getBus(token).awaitResponse() },
        transformSuccess = { _,json -> parseBus(json) }
    )
    private fun parseBus(result : String) : List<BusBean> = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, BusResponse::class.java).result
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
        request = { (SemseterParser.getSemseter().plus(20)).toString().let { jxglstu.getLessonIds(cookie,bizTypeId.toString(), it,studentId.toString()).awaitResponse() }},
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
            val call = jxglstu.getDatum(cookie,jsonObject)

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
    suspend fun getWeatherWarn(campus: CampusRegion) = QWeatherRepository.getWeatherWarn(campus,weatherWarningData)
    val qWeatherResult = StateHolder<QWeatherNowBean>()
    suspend fun getWeather(campus: CampusRegion) = QWeatherRepository.getWeather(campus,qWeatherResult)
// 学工系统/今日校园 ////////////////////////////////////////////////////////////////////////////////////////////////

    suspend fun goToStu(cookie : String) = launchRequestNone {
        login.loginGoTo(service = LoginType.STU.service, cookie = cookie).awaitResponse()
    }

//    fun getStuInfo(cookie: String) = stu.getStudentInfo(cookie)
// 宣城校园网 ////////////////////////////////////////////////////////////////////////////////////////////////

    val loginSchoolNetResponse = StateHolder<Boolean>()
    suspend fun loginSchoolNet(campus: CampusRegion = getCampusRegion()) = LoginSchoolNetRepository.loginSchoolNet(campus,loginSchoolNetResponse)
    suspend fun logoutSchoolNet(campus: CampusRegion = getCampusRegion()) = LoginSchoolNetRepository.logoutSchoolNet(campus,loginSchoolNetResponse)

    val infoWebValue = StateHolder<WebInfo>()
    suspend fun getWebInfo() = LoginSchoolNetRepository.getWebInfo(infoWebValue)
    suspend fun getWebInfo2() = LoginSchoolNetRepository.getWebInfo2(infoWebValue)

    val giteeUpdatesResp = StateHolder<GiteeReleaseResponse>()
    suspend fun getUpdate() = GithubRepository.getUpdate(giteeUpdatesResp)
}


package com.hfut.schedule.logic.network.repo

import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.App.MyApplication.Companion.ADMISSION_COOKIE_HEADER
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.AdmissionType
import com.hfut.schedule.logic.model.AcademicNewsResponse
import com.hfut.schedule.logic.model.AcademicType
import com.hfut.schedule.logic.model.AcademicXCType
import com.hfut.schedule.logic.model.AdmissionDetailBean
import com.hfut.schedule.logic.model.AdmissionDetailResponseHistory
import com.hfut.schedule.logic.model.AdmissionDetailResponsePlan
import com.hfut.schedule.logic.model.AdmissionListResponse
import com.hfut.schedule.logic.model.AdmissionMapBean
import com.hfut.schedule.logic.model.AdmissionTokenResponse
import com.hfut.schedule.logic.model.ForecastAllBean
import com.hfut.schedule.logic.model.ForecastResponse
import com.hfut.schedule.logic.model.GithubBean
import com.hfut.schedule.logic.model.HaiLeDeviceDetailBean
import com.hfut.schedule.logic.model.HaiLeDeviceDetailRequestBody
import com.hfut.schedule.logic.model.HaiLeDeviceDetailResponse
import com.hfut.schedule.logic.model.HaiLeNearPositionBean
import com.hfut.schedule.logic.model.HaiLeNearPositionRequestDTO
import com.hfut.schedule.logic.model.HaiLeNearPositionResponse
import com.hfut.schedule.logic.model.NewsResponse
import com.hfut.schedule.logic.model.OfficeHallSearchBean
import com.hfut.schedule.logic.model.OfficeHallSearchResponse
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.model.QWeatherResponse
import com.hfut.schedule.logic.model.QWeatherWarnBean
import com.hfut.schedule.logic.model.QWeatherWarnResponse
import com.hfut.schedule.logic.model.SearchEleResponse
import com.hfut.schedule.logic.model.TeacherResponse
import com.hfut.schedule.logic.model.WorkSearchResponse
import com.hfut.schedule.logic.model.XuanquResponse
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.model.guagua.GuaguaBillsResponse
import com.hfut.schedule.logic.model.guagua.UseCodeResponse
import com.hfut.schedule.logic.model.toVercelForecastRequestBody
import com.hfut.schedule.logic.model.wx.WXClassmatesBean
import com.hfut.schedule.logic.model.wx.WXClassmatesResponse
import com.hfut.schedule.logic.model.wx.WXLoginResponse
import com.hfut.schedule.logic.model.wx.WXPersonInfoBean
import com.hfut.schedule.logic.model.wx.WXPersonInfoResponse
import com.hfut.schedule.logic.model.wx.WXQrCodeLoginResponse
import com.hfut.schedule.logic.model.wx.WXQrCodeResponse
import com.hfut.schedule.logic.model.zjgd.BillBean
import com.hfut.schedule.logic.network.api.AcademicService
import com.hfut.schedule.logic.network.api.AcademicXCService
import com.hfut.schedule.logic.network.api.AdmissionService
import com.hfut.schedule.logic.network.api.DormitoryScore
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.GithubRawService
import com.hfut.schedule.logic.network.api.GithubService
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.api.HaiLeWashingService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.network.api.NewsService
import com.hfut.schedule.logic.network.api.OfficeHallService
import com.hfut.schedule.logic.network.api.QWeatherService
import com.hfut.schedule.logic.network.api.TeachersService
import com.hfut.schedule.logic.network.api.VercelForecastService
import com.hfut.schedule.logic.network.api.WXService
import com.hfut.schedule.logic.network.api.WorkService
import com.hfut.schedule.logic.network.api.XuanChengService
import com.hfut.schedule.logic.network.servicecreator.AcademicServiceCreator
import com.hfut.schedule.logic.network.servicecreator.AcademicXCServiceCreator
import com.hfut.schedule.logic.network.servicecreator.AdmissionServiceCreator
import com.hfut.schedule.logic.network.servicecreator.DormitoryScoreServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubRawServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.servicecreator.HaiLeWashingServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebHefeiServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.network.servicecreator.NewsServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OfficeHallServiceCreator
import com.hfut.schedule.logic.network.servicecreator.QWeatherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.TeacherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.VercelForecastServiceCreator
import com.hfut.schedule.logic.network.servicecreator.WXServiceCreator
import com.hfut.schedule.logic.network.servicecreator.WorkServiceCreator
import com.hfut.schedule.logic.network.servicecreator.XuanChengServiceCreator
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.state.PARSE_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.WebInfo
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.GithubFolderBean
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.util.network.state.CONNECTION_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.OPERATION_FAST_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.TIMEOUT_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.UNKNOWN_ERROR_CODE
import com.hfut.schedule.ui.component.status.ErrorUI
import com.hfut.schedule.ui.component.status.StatusUI
import com.hfut.schedule.ui.screen.home.search.function.other.life.getLocation
import com.hfut.schedule.ui.screen.news.home.transferToPostData
import com.hfut.schedule.ui.screen.shower.home.function.StatusMsgResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.awaitResponse
import java.util.Locale

// Repo迁移计划
object Repository {
    private val loginWebHefei = LoginWebHefeiServiceCreator.create(LoginWebsService::class.java)
    private val loginWeb = LoginWebServiceCreator.create(LoginWebsService::class.java)
    private val loginWeb2 = LoginWeb2ServiceCreator.create(LoginWebsService::class.java)
    private val teacher = TeacherServiceCreator.create(TeachersService::class.java)
    private val xuanCheng = XuanChengServiceCreator.create(XuanChengService::class.java)
    private val workSearch = WorkServiceCreator.create(WorkService::class.java)
    private val news = NewsServiceCreator.create(NewsService::class.java)
    private val xuanChengDormitory = DormitoryScoreServiceCreator.create(DormitoryScore::class.java)
    private val qWeather = QWeatherServiceCreator.create(QWeatherService::class.java)
    private val github = GithubServiceCreator.create(GithubService::class.java)
    private val gitee = GiteeServiceCreator.create(GiteeService::class.java)
    private val githubRaw = GithubRawServiceCreator.create(GithubRawService::class.java)
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
    private val forecast = VercelForecastServiceCreator.create(VercelForecastService::class.java)
    private val academic = AcademicServiceCreator.create(AcademicService::class.java)
    private val academicXC = AcademicXCServiceCreator.create(AcademicXCService::class.java)
    private val haiLe = HaiLeWashingServiceCreator.create(HaiLeWashingService::class.java)
    private val admission = AdmissionServiceCreator.create(AdmissionService::class.java)
    private val wx = WXServiceCreator.create(WXService::class.java)
    private val hall = OfficeHallServiceCreator.create(OfficeHallService::class.java)

    //引入接口
    // 通用的网络请求方法，支持自定义的操作
    @JvmStatic
    fun <T> makeRequest(
        call: Call<ResponseBody>,
        liveData: (MutableLiveData<T>)? = null,
        onSuccess: ((Response<ResponseBody>) -> Unit)? = null
    ) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && liveData != null) {
                    val responseBody = response.body()?.string()
                    val result: T? = parseResponse(responseBody)
                    liveData.value = result
                }

                // 执行自定义操作
                onSuccess?.invoke(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    // 通用方法用于解析响应（根据需要进行调整）
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseResponse(responseBody: String?): T? {
        return responseBody as? T
    }

    @JvmStatic
    suspend fun <T> launchRequestSimple(
        holder: StateHolder<T>,
        request: suspend () -> Response<ResponseBody>,
        transformSuccess: suspend (Headers, String) -> T,
        transformRedirect: ((Headers) -> T)? = null
    ) = try {
        holder.setLoading()
        val response = request()
        val headers = response.headers()
        val bodyString = response.body()?.string().orEmpty()

        if (response.isSuccessful) {
            // 成功
            val result = try {
                transformSuccess(headers, bodyString)
            } catch (e: Exception) {
                holder.emitError(e, PARSE_ERROR_CODE)
                return
            }
            holder.emitData(result)
        }
        else if(response.code() == 302){
//             重定向 特殊处理
            val result = try {
                transformRedirect!!(headers)
            } catch (e: Exception) {
                holder.emitError(e, PARSE_ERROR_CODE)
                return
            }
            holder.emitData(result)
        }
        else {
            // 承接错误解析 可选
            holder.emitError(HttpException(response), response.code())
        }
    } catch (e: Exception) {
        holder.emitError(e,null)
    }
    @JvmStatic
    suspend fun <T> launchRequestSimple(
        holder: StateHolder<T>,
        request: suspend () -> Response<Void>,
        transformSuccess: suspend (Headers) -> T,
        transformRedirect: ((Headers) -> T)? = null
    ) = try {
        holder.setLoading()
        val response = request()
        val headers = response.headers()

        if (response.isSuccessful) {
            // 成功
            val result = try {
                transformSuccess(headers)
            } catch (e: Exception) {
                holder.emitError(e, PARSE_ERROR_CODE)
                return
            }
            holder.emitData(result)
        }
        else if(response.code() == 302){
//             重定向 特殊处理
            val result = try {
                transformRedirect!!(headers)
            } catch (e: Exception) {
                holder.emitError(e, PARSE_ERROR_CODE)
                return
            }
            holder.emitData(result)
        }
        else {
            // 承接错误解析 可选
            holder.emitError(HttpException(response), response.code())
        }
    } catch (e: Exception) {
        holder.emitError(e,null)
    }

    @JvmStatic
    suspend fun launchRequestNone(
        request: suspend () -> Response<ResponseBody>,
    ) : Int = try {
        val response = request()
        response.code()
    } catch (e : Exception) {
        e.printStackTrace()
        val eMsg = e.message
        if(eMsg?.contains("10000ms") == true) {
            TIMEOUT_ERROR_CODE
        } else if(eMsg?.contains("Unable to resolve host",ignoreCase = true) == true || eMsg?.contains("Failed to connect to",ignoreCase = true) == true ||  eMsg?.contains("Connection reset",ignoreCase = true) == true) {
            CONNECTION_ERROR_CODE
        } else if(eMsg?.contains("The coroutine scope") == true) {
            OPERATION_FAST_ERROR_CODE
        } else {
            UNKNOWN_ERROR_CODE
        }
    }

    suspend fun officeHallSearch(
        text : String,
        page : Int,
        holder : StateHolder<List<OfficeHallSearchBean>>
    ) = launchRequestSimple(
        holder = holder,
        request = { hall.search(
            name = text,
            page = page,
            pageSize = prefs.getString("OfficeHallRequest",MyApplication.DEFAULT_PAGE_SIZE.toString())?.toInt() ?: MyApplication.DEFAULT_PAGE_SIZE,
        ).awaitResponse() },
        transformSuccess = { _,json -> parseOfficeHallSearch(json) }
    )
    @JvmStatic
    private suspend fun parseOfficeHallSearch(json : String) : List<OfficeHallSearchBean> = try {
        Gson().fromJson(json, OfficeHallSearchResponse::class.java).data.records
    } catch (e : Exception) { throw e }


    suspend fun wxLogin(holder : StateHolder<String>) = launchRequestSimple(
        holder = holder,
        request = { wx.login().awaitResponse() },
        transformSuccess = { _,json -> parseWxLogin(json) }
    )
    @JvmStatic
    private suspend fun parseWxLogin(json : String) : String = try {
        val bean = Gson().fromJson(json, WXLoginResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            // 保存
            val auth = bean.data.TGT
            DataStoreManager.saveWxAuth(auth)
            auth
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxGetPersonInfo(auth : String,holder : StateHolder<WXPersonInfoBean>) = launchRequestSimple(
        holder = holder,
        request = { wx.getMyInfo(auth).awaitResponse() },
        transformSuccess = { _,json -> parseWxPersonInfo(json) }
    )
    @JvmStatic
    private suspend fun parseWxPersonInfo(json : String) : WXPersonInfoBean = try {
        val bean = Gson().fromJson(json, WXPersonInfoResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            saveString("WX_PERSON_INFO", json)
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxGetClassmates(nodeId : String,auth : String,holder : StateHolder<WXClassmatesBean>) = launchRequestSimple(
        holder = holder,
        request = { wx.getClassmates(nodeId,auth).awaitResponse() },
        transformSuccess = { _,json -> parseWxClassmates(json) }
    )
    @JvmStatic
    private suspend fun parseWxClassmates(json : String) : WXClassmatesBean = try {
        val bean = Gson().fromJson(json, WXClassmatesResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }

    suspend fun wxLoginCas(url : String,auth : String,holder : StateHolder<Pair<String, Boolean>>) = launchRequestSimple(
        holder = holder,
        request = {
            // 先解析原 URL
            val originalUri = url.toUri()
            // 用原路径和查询参数替换 host
            val newUrl = originalUri.buildUpon()
                .encodedAuthority(MyApplication.WX_URL.toUri().encodedAuthority)
                .scheme(MyApplication.WX_URL.toUri().scheme)
                .build()
                .toString()
            // 处理URL 将其HOST换成
            // 然后发送网络请求 GET 携带 @Header("Authorization") auth : String
            wx.loginCas(newUrl,auth).awaitResponse()
         },
        transformSuccess = { _,json -> parseWxLoginCas(json) }
    )

    @JvmStatic
    private fun parseWxLoginCas(json : String) : Pair<String, Boolean> = try {
        val bean = Gson().fromJson(json, WXQrCodeResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            Pair("扫码成功",true)
        } else {
            Pair(msg,false)
        }
    } catch (e : Exception) { throw e }


    suspend fun wxConfirmLogin(uuid : String,auth : String,holder : StateHolder<String>) = launchRequestSimple(
        holder = holder,
        request = { wx.confirmLogin(uuid,auth).awaitResponse() },
        transformSuccess = { _,json -> parseWxConfirmLogin(json) }
    )
    @JvmStatic
    private suspend fun parseWxConfirmLogin(json : String) : String = try {
        val bean = Gson().fromJson(json, WXQrCodeLoginResponse::class.java)
        val msg = bean.msg
        if(msg.contains("success")) {
            bean.data
        } else {
            throw Exception(msg)
        }
    } catch (e : Exception) { throw e }

    suspend fun loginSchoolNet(campus: CampusRegion = getCampusRegion(), loginSchoolNetResponse : StateHolder<Boolean>) =
        withContext(Dispatchers.IO) {
            getPersonInfo().studentId?.let { uid ->
                getCardPsk()?.let { pwd ->
                    when (campus) {
                        CampusRegion.HEFEI -> {
                            val location = "123"
                            launchRequestSimple(
                                holder = loginSchoolNetResponse,
                                request = {
                                    loginWebHefei.loginWeb(uid, pwd, location).awaitResponse()
                                },
                                transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                            )
                        }

                        CampusRegion.XUANCHENG -> {
                            val location = "宣州Login"
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = {
                                        loginWeb.loginWeb(uid, pwd, location).awaitResponse()
                                    },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = {
                                        loginWeb2.loginWeb(uid, pwd, location).awaitResponse()
                                    },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                        }
                    }
                }
            }
        }
    suspend fun logoutSchoolNet(campus: CampusRegion = getCampusRegion(), loginSchoolNetResponse : StateHolder<Boolean>) =
        withContext(Dispatchers.IO) {
            getPersonInfo().studentId?.let { uid ->
                getCardPsk()?.let { pwd ->
                    when (campus) {
                        CampusRegion.HEFEI -> {
                            val location = "123"
                            launchRequestSimple(
                                holder = loginSchoolNetResponse,
                                request = {
                                    loginWebHefei.loginWeb(uid, pwd, location).awaitResponse()
                                },
                                transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                            )
                        }

                        CampusRegion.XUANCHENG -> {
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = { loginWeb.logoutWeb().awaitResponse() },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = { loginWeb2.logoutWeb().awaitResponse() },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                        }
                    }
                }
            }
        }
    // 目前仅适配了宣区
    @JvmStatic
    private fun parseLoginSchoolNet(result : String) : Boolean = try {
        if(result.contains("成功") && !result.contains("已使用")) {
            true
        } else if(result.contains("已使用")) {
            false
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    suspend fun getWebInfo(infoWebValue : StateHolder<WebInfo>) = launchRequestSimple(
        holder = infoWebValue,
        request = { loginWeb.getInfo().awaitResponse() },
        transformSuccess = { _,json -> parseWebInfo(json) }
    )

    suspend fun getWebInfo2(infoWebValue : StateHolder<WebInfo>) = launchRequestSimple(
        holder = infoWebValue,
        request = { loginWeb2.getInfo().awaitResponse() },
        transformSuccess = { _,json -> parseWebInfo(json) }
    )
    @JvmStatic
    private fun parseWebInfo(html : String) : WebInfo = try {
        //本段照搬前端
        val flow = html.substringAfter("flow").substringBefore(" ").substringAfter("'").toDouble()
        val fee = html.substringAfter("fee").substringBefore(" ").substringAfter("'").toDouble()
        var flow0 = flow % 1024
        val flow1 = flow - flow0
        flow0 *= 1000
        flow0 -= flow0 % 1024
        var fee1 = fee - fee % 100
        var flow3 = "."
        if (flow0 / 1024 < 10) flow3 = ".00"
        else { if (flow0 / 1024 < 100) flow3 = ".0"; }
        val resultFee = (fee1 / 10000).toString()
        val resultFlow : String = ((flow1 / 1024).toString() + flow3 + (flow0 / 1024)).substringBefore(".")
        val result = WebInfo(resultFee,resultFlow)
        saveString("memoryWeb", result.flow)
        result
    } catch (e : Exception) { throw e }

    suspend fun searchTeacher(name: String = "", direction: String = "",teacherSearchData : StateHolder<TeacherResponse>) = launchRequestSimple(
        holder = teacherSearchData,
        request = { teacher.searchTeacher(name=name, direction = direction, size = prefs.getString("TeacherSearchRequest",MyApplication.DEFAULT_PAGE_SIZE.toString()) ?: MyApplication.DEFAULT_PAGE_SIZE.toString() ).awaitResponse() },
        transformSuccess = { _,json -> parseTeacherSearch(json) }
    )

    @JvmStatic
    private fun parseTeacherSearch(json : String) : TeacherResponse = try {
        Gson().fromJson(json, TeacherResponse::class.java)
    } catch (e : Exception) { throw e }


    suspend fun getAdmissionList(type : AdmissionType,holder : StateHolder<Pair<AdmissionType,Map<String, List<AdmissionMapBean>>>>) = launchRequestSimple(
        holder = holder,
        request = {  admission.getList(type.type).awaitResponse() },
        transformSuccess = { _,json -> parseAdmissionList(type,json) }
    )

    @JvmStatic
    private fun parseAdmissionList(type: AdmissionType,json : String) : Pair<AdmissionType, Map<String, List<AdmissionMapBean>>> = try {
        Pair(type,Gson().fromJson(json, AdmissionListResponse::class.java).data.list)
    } catch (e : Exception) { throw e }

    suspend fun getAdmissionDetail(type : AdmissionType,bean : AdmissionMapBean,region: String,holder : StateHolder<AdmissionDetailBean>,tokenHolder : StateHolder<AdmissionTokenResponse>) =
        onListenStateHolderForNetwork(tokenHolder,holder) { token ->
            launchRequestSimple(
                holder = holder,
                request = {  admission.getDetail(type.type,region,bean.year,bean.subject,bean.campus,bean.type,ADMISSION_COOKIE_HEADER + token.cookie,token.data).awaitResponse() },
                transformSuccess = { _,json -> parseAdmissionDetail(type,json) }
            )
        }

    @JvmStatic
    private fun parseAdmissionDetail(type : AdmissionType,json : String) : AdmissionDetailBean = try {
        when(type) {
            AdmissionType.HISTORY -> {
                val parsed = Gson().fromJson(json, AdmissionDetailResponseHistory::class.java)
                AdmissionDetailBean.History(parsed.data)
            }
            AdmissionType.PLAN -> {
                val parsed = Gson().fromJson(json, AdmissionDetailResponsePlan::class.java)
                AdmissionDetailBean.Plan(parsed.data)
            }
        }
    } catch (e : Exception) { throw e }



    suspend fun getAdmissionToken(holder : StateHolder<AdmissionTokenResponse>) = launchRequestSimple(
        holder = holder,
        request = {
            val state = holder.state.first()
            val cookie = if(state !is UiState.Success) {
                ""
            } else {
                ADMISSION_COOKIE_HEADER + state.data.cookie
            }
            admission.getToken(cookie = cookie).awaitResponse()
        },
        transformSuccess = { _,json -> parseAdmissionToken(json) }
    )

    @JvmStatic
    private fun parseAdmissionToken(json : String) : AdmissionTokenResponse = try {
        Gson().fromJson(json, AdmissionTokenResponse::class.java)
    } catch (e : Exception) { throw e }

    fun searchXuanChengNews(title : String, page: Int = 1) {

        val postData = transferToPostData(title, page)
        val call = xuanCheng.searchNotications(postData)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                newsXuanChengResult.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    suspend fun getXuanChengNews(page: Int,newsXuanChengResult : StateHolder<List<NewsResponse>>) = launchRequestSimple(
        holder = newsXuanChengResult,
        request = { xuanCheng.getNotications(page = page.let { if(it <= 1)  ""  else  it.toString()  }).awaitResponse() },
        transformSuccess = { _,html -> parseNewsXuanCheng(html) }
    )

    @JvmStatic
    private fun parseNewsXuanCheng(html : String) : List<NewsResponse> = try {
        val document = Jsoup.parse(html)
        document.select("ul.news_list > li").map { element ->
            val titleElement = element.selectFirst("span.news_title a")
            val title = titleElement?.attr("title") ?: "未知标题"
            val url = titleElement?.attr("href") ?: "未知URL"
            val date = element.selectFirst("span.news_meta")?.text() ?: "未知日期"

            NewsResponse(title, date, url)
        }
    } catch (e : Exception) { throw e }

    suspend fun searchWorks(keyword: String?, page: Int = 1, type: Int, campus: CampusRegion, workSearchResult : StateHolder<WorkSearchResponse>) = launchRequestSimple(
        holder = workSearchResult,
        request = {
            workSearch.search(
                keyword = keyword,
                page = page,
                pageSize = prefs.getString("WorkSearchRequest",MyApplication.DEFAULT_PAGE_SIZE.toString())?.toIntOrNull() ?: MyApplication.DEFAULT_PAGE_SIZE,
                type = type.let { if(it == 0) null else it },
                token = "yxqqnn1700000" + if(campus == CampusRegion.XUANCHENG) "119" else "002"
            ).awaitResponse() },
        transformSuccess = { _, json -> parseWorkResponse(json) },
    )

    @JvmStatic
    private fun parseWorkResponse(resp : String): WorkSearchResponse = try {
        // 去掉前缀，提取 JSON 部分
        val jsonStr = resp.removePrefix("var __result = ").removeSuffix(";").trim()
        Gson().fromJson(jsonStr,WorkSearchResponse::class.java)
    } catch (e : Exception) { throw e }

    suspend fun searchNews(title : String,page: Int = 1,newsResult : StateHolder<List<NewsResponse>>) = launchRequestSimple(
        holder = newsResult,
        request = { news.searchNews(Encrypt.encodeToBase64(title),page).awaitResponse() },
        transformSuccess = { _,html -> parseNews(html) }
    )

    @JvmStatic
    private fun parseNews(html : String) : List<NewsResponse> = try {
        var newsList = mutableListOf<NewsResponse>()
        val doc: Document = Jsoup.parse(html)
        val newsItems = doc.select("ul.list li")

        for (item in newsItems) {
            val date = item.select("i.timefontstyle252631").text()
            val title = item.select("p.titlefontstyle252631").text()
            val link = item.select("a").attr("href")
            if(title.isEmpty() || title.isBlank()) {
                break
            }
            newsList.add(NewsResponse(title, date, link))
        }
        // 去重
        newsList = newsList.distinctBy { it.title + it.link + it.date }.toMutableList()
        newsList
    } catch (e : Exception) { throw e }

    suspend fun getAcademic(type: AcademicType, totalPage : Int? = null,page: Int = 1,holder : StateHolder<AcademicNewsResponse>) = launchRequestSimple(
        holder = holder,
        request = {
            if(totalPage == null || totalPage == page) {
                academic.getNews("${type.type}.htm").awaitResponse()
            } else {
                academic.getNews("${type.type}/${totalPage - page + 1}.htm").awaitResponse()
            }
        },
        transformSuccess = { _, json -> parseAcademicNews(json) },
    )
    @JvmStatic
    private fun parseAcademicNews(html : String) : AcademicNewsResponse = try {
        val document: Document = Jsoup.parse(html)

        // 提取新闻列表
        val newsList = mutableListOf<NewsResponse>()
        val newsElements = document.select("a.l3-news--item")

        for (element in newsElements) {
            val link = element.attr("href")  // 相对链接，可拼接 baseUrl
            val title = element.selectFirst("div.l3-news--title")?.text() ?: ""
            val date = element.selectFirst("div.l3-news--month")?.text() ?: ""

            newsList.add(NewsResponse(title = title, date = date, link = link))
        }

        // 提取总页数，例如最后的“110”
        val pageText = document.select("span.p_no a").map { it.text() }
        val maxPage = pageText.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 1

        AcademicNewsResponse(news = newsList, totalPage = maxPage)
    } catch (e : Exception) { throw e }


    suspend fun getAcademicXC(type: AcademicXCType,page: Int = 1,holder : StateHolder<List<NewsResponse>>) = launchRequestSimple(
        holder = holder,
        request = { academicXC.getNews(type.type,page).awaitResponse() },
        transformSuccess = { _, json -> parseAcademicNewsXC(json) },
    )
    @JvmStatic
    private fun parseAcademicNewsXC(html : String) : List<NewsResponse> = try {
        val document = Jsoup.parse(html)
        val newsList = mutableListOf<NewsResponse>()

        // 找到所有<tr class="articlelist2_tr">
        val rows = document.select("tr.articlelist2_tr")
        for (row in rows) {
            val aTag = row.selectFirst("a.articlelist1_a_title")
            val dateTd = row.selectFirst("td[align=right]")

            if (aTag != null && dateTd != null) {
                val title = aTag.attr("title").replace("\u00a0", " ") // 替换不间断空格
                val link = aTag.attr("href")
                val date = dateTd.text()

                newsList.add(NewsResponse(title, date, link))
            }
        }

        newsList
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseElectric(result : String) : String = try {
        if (result.contains("query_elec_roominfo")) {
            var msg = Gson().fromJson(result, SearchEleResponse::class.java).query_elec_roominfo.errmsg

            if(msg.contains("剩余金额"))
                formatDecimal(msg.substringAfter("剩余金额").substringAfter(":").toDouble(),2)
            else
                throw Exception(msg)
        }
        else
            throw Exception(result)
    } catch (e : Exception) { throw e }

    suspend fun searchDormitoryXuanCheng(code : String,dormitoryResult : StateHolder<List<XuanquResponse>>) = launchRequestSimple(
        holder = dormitoryResult,
        request = { xuanChengDormitory.search(code).awaitResponse() },
        transformSuccess = { _,html -> parseDormitoryXuanCheng(html) }
    )

    @JvmStatic
    private fun parseDormitoryXuanCheng(html : String) : List<XuanquResponse> = try {
        // 定义一个正则表达式来匹配HTML标签
        val regex = """<td rowspan="(\d+)">(\d+)</td>\s*<td>(\d+)</td>\s*<td>(\d+)</td>\s*<td rowspan="\d+">(\d{4}-\d{2}-\d{2})</td>""".toRegex()

        val data = html.let {
            regex.findAll(it).map {
                XuanquResponse(score = it.groupValues[2].toInt(), date = it.groupValues[5])
            }.toList()
        }
        data
    }  catch (e : Exception) { throw e }

    suspend fun getWeatherWarn(campus: CampusRegion, weatherWarningData : StateHolder<List<QWeatherWarnBean>>) = launchRequestSimple(
        holder = weatherWarningData,
        request = { qWeather.getWeatherWarn(locationID = getLocation(campus)).awaitResponse() },
        transformSuccess = { _,json -> parseWeatherWarn(json) }
    )

    @JvmStatic
    private fun parseWeatherWarn(json : String) : List<QWeatherWarnBean> = try {
        Gson().fromJson(json, QWeatherWarnResponse::class.java).warning
    } catch (e : Exception) { throw e }

    suspend fun getWeather(campus: CampusRegion, qWeatherResult : StateHolder<QWeatherNowBean>) = launchRequestSimple(
        holder = qWeatherResult,
        request = { qWeather.getWeather(locationID = getLocation(campus)).awaitResponse() },
        transformSuccess = { _, json -> parseWeatherNow(json) }
    )


    @JvmStatic
    private fun parseWeatherNow(json : String) : QWeatherNowBean = try {
        if(json.contains("200"))
            Gson().fromJson(json, QWeatherResponse::class.java).now
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    suspend fun getStarNum(githubStarsData : StateHolder<Int>) = launchRequestSimple(
        holder = githubStarsData,
        request = { github.getRepoInfo().awaitResponse() },
        transformSuccess = { _,json -> parseGithubStarNum(json) }
    )

    @JvmStatic
    private fun parseGithubStarNum(json : String) : Int = try {
        Gson().fromJson(json,GithubBean::class.java).stargazers_count
    } catch (e : Exception) { throw e }

    suspend fun getUpdateContents(holder : StateHolder<List<GithubFolderBean>>) = launchRequestSimple(
        holder = holder,
        request = { github.getFolderContent().awaitResponse() },
        transformSuccess = { _,json -> parseUpdateContents(json) }
    )

    @JvmStatic
    private fun parseUpdateContents(json : String) : List<GithubFolderBean> = try {
        val listType = object : TypeToken<List<GithubFolderBean>>() {}.type
        val data : List<GithubFolderBean> = Gson().fromJson(json,listType)
        data
    } catch (e : Exception) { throw e }


    fun getUpdate() {
        val call = gitee.getUpdate()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("versions",response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun downloadHoliday()  {
        val call = githubRaw.getYearHoliday()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("HOLIDAY", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    suspend fun getHaiLeNear(bean : HaiLeNearPositionRequestDTO,holder : StateHolder<List<HaiLeNearPositionBean>>) = launchRequestSimple(
        holder = holder,
        request = { haiLe.getNearPlaces(bean.toRequestBody()).awaitResponse() },
        transformSuccess = { _, json -> parseHaiLeNear(json) }
    )

    @JvmStatic
    private fun parseHaiLeNear(result: String): List<HaiLeNearPositionBean> = try {
        if(result.contains("success")) {
            Gson().fromJson(result, HaiLeNearPositionResponse::class.java).data.items
        } else {
            throw Exception(result)
        }
    } catch (e: Exception) { throw e }


    suspend fun getUpdateFileSize(fileName : String,holder : StateHolder<Double>) = launchRequestSimple(
        holder = holder,
        request = { gitee.download(fileName).awaitResponse() },
        transformSuccess = { headers -> parseGiteeFileSize(headers) }
    )

    @JvmStatic
    private fun parseGiteeFileSize(headers: Headers): Double = try {
        val contentLength = headers["Content-Length"]?.toLongOrNull() ?: throw Exception("无法获取文件")
        contentLength.toDouble() / (1024 * 1024)
    } catch (e: Exception) { throw e }


    suspend fun getHaiLDeviceDetail(bean : HaiLeDeviceDetailRequestBody,holder : StateHolder<List<HaiLeDeviceDetailBean>>) = launchRequestSimple(
        holder = holder,
        request = { haiLe.getDeviceDetail(bean).awaitResponse() },
        transformSuccess = { _, json -> parseHaiLeDeviceDetail(json) }
    )

    @JvmStatic
    private fun parseHaiLeDeviceDetail(result: String): List<HaiLeDeviceDetailBean> = try {
        if(result.contains("success")) {
            Gson().fromJson(result, HaiLeDeviceDetailResponse::class.java).data.items
        } else {
            throw Exception(result)
        }
    } catch (e: Exception) { throw e }

    suspend fun guaGuaLogin(phoneNumber : String, password : String,loginResult : StateHolder<GuaGuaLoginResponse>) = launchRequestSimple(
        holder = loginResult,
        request = { guaGua.login(phoneNumber,password).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaLogin(json) },
    )

    suspend fun guaGuaStartShower(phoneNumber: String, macLocation : String, loginCode : String,startShowerResult : StateHolder<String>) = launchRequestSimple(
        holder = startShowerResult,
        request = { guaGua.startShower(phoneNumber = phoneNumber,loginCode = loginCode,macLocation = macLocation).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaStartShower(json) },
    )

    suspend fun guaGuaGetBills(billsResult : StateHolder<GuaguaBillsResponse>) = launchRequestSimple(
        holder = billsResult,
        request = { guaGua.getBills(phoneNumber= prefs.getString("PHONENUM","") ?: "", loginCode= prefs.getString("loginCode","") ?: "").awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaBills(json) },
    )

    suspend fun guaGuaGetUseCode(useCodeResult : StateHolder<String>) = launchRequestSimple(
        holder = useCodeResult,
        request = { guaGua.getUseCode(phoneNumber= prefs.getString("PHONENUM","") ?: "",loginCode= prefs.getString("loginCode","") ?: "").awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaUseCode(json) }
    )

    suspend fun guaGuaReSetUseCode(newCode : String,reSetCodeResult : StateHolder<String>) = launchRequestSimple(
        holder = reSetCodeResult,
        request = {
            val psk = prefs.getString("GuaGuaPsk","") ?: ""
            val encrypted = Encrypt.md5Hash(psk).uppercase(Locale.ROOT)
            guaGua.reSetUseCode(phoneNumber= prefs.getString("PHONENUM","") ?: "", encrypted, loginCode= prefs.getString("loginCode","") ?: "", newCode).awaitResponse() },
        transformSuccess = { _, json -> parseGuaGuaReSetUseCode(json) }
    )

    @JvmStatic
    private fun parseGuaGuaLogin(result: String): GuaGuaLoginResponse = try {
        val data = Gson().fromJson(result, GuaGuaLoginResponse::class.java)
        if(data.message.contains("成功")) {
            saveString("GuaGuaPersonInfo",result)
            saveString("loginCode",data.data?.loginCode)
        }
        data
    } catch (e: Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaStartShower(result: String): String = try {
        Gson().fromJson(result, StatusMsgResponse::class.java).message
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaBills(result: String) : GuaguaBillsResponse = try {
        Gson().fromJson(result, GuaguaBillsResponse::class.java)
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaUseCode(result: String) : String = try {
        if(result.contains("成功"))
            Gson().fromJson(result,UseCodeResponse::class.java).data.randomCode
        else throw Exception("解析错误")
    } catch (e : Exception) { throw e }

    @JvmStatic
    private fun parseGuaGuaReSetUseCode(result: String) : String = try {
        Gson().fromJson(result,StatusMsgResponse::class.java).message
    } catch (e : Exception) { throw e }

    suspend fun getCardPredicted(bean : BillBean,cardPredictedData : StateHolder<ForecastAllBean>) = launchRequestSimple(
        holder = cardPredictedData,
        request = { forecast.getData(toVercelForecastRequestBody(bean)).awaitResponse() },
        transformSuccess = { _,json -> parseCardPredicted(json) }
    )

    @JvmStatic
    private fun parseCardPredicted(json : String) : ForecastAllBean = try {
        val data = Gson().fromJson(json,ForecastResponse::class.java)
        if(data.state == 200) {
            data.data
        } else {
            throw Exception(data.msg ?: "错误")
        }
    } catch (e : Exception) { throw e }
}
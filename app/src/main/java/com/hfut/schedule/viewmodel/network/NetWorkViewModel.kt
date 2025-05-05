package com.hfut.schedule.viewmodel.network

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.enumeration.LibraryItems
import com.hfut.schedule.logic.enumeration.LoginType
import com.hfut.schedule.logic.model.NewsResponse
import com.hfut.schedule.logic.model.QWeatherNowBean
import com.hfut.schedule.logic.model.QWeatherResponse
import com.hfut.schedule.logic.model.SupabaseEventForkCount
import com.hfut.schedule.logic.model.SupabaseEventOutput
import com.hfut.schedule.logic.model.SupabaseRefreshLoginBean
import com.hfut.schedule.logic.model.SupabaseUserLoginBean
import com.hfut.schedule.logic.model.TeacherResponse
import com.hfut.schedule.logic.model.WorkSearchResponse
import com.hfut.schedule.logic.model.XuanquNewsItem
import com.hfut.schedule.logic.model.jxglstu.MyApplyResponse
import com.hfut.schedule.logic.model.jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.model.jxglstu.SurveyTeacherResponse
import com.hfut.schedule.logic.model.jxglstu.TransferResponse
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessonSurveyTasks
import com.hfut.schedule.logic.model.one.BorrowBooksResponse
import com.hfut.schedule.logic.model.one.SubBooksResponse
import com.hfut.schedule.logic.model.one.getTokenResponse
import com.hfut.schedule.logic.model.zjgd.BillDatas
import com.hfut.schedule.logic.model.zjgd.BillMonth
import com.hfut.schedule.logic.model.zjgd.BillMonthResponse
import com.hfut.schedule.logic.model.zjgd.BillRangeResponse
import com.hfut.schedule.logic.model.zjgd.BillResponse
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.model.zjgd.FeeType.ELECTRIC
import com.hfut.schedule.logic.model.zjgd.FeeType.SHOWER
import com.hfut.schedule.logic.model.zjgd.FeeType.WEB
import com.hfut.schedule.logic.model.zjgd.PayStep1Response
import com.hfut.schedule.logic.model.zjgd.PayStep2Response
import com.hfut.schedule.logic.model.zjgd.PayStep3Response
import com.hfut.schedule.logic.network.api.CommunityService
import com.hfut.schedule.logic.network.api.DormitoryScore
import com.hfut.schedule.logic.network.api.FWDTService
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.GithubRawService
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LePaoYunService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.NewsService
import com.hfut.schedule.logic.network.api.OneService
import com.hfut.schedule.logic.network.api.QWeatherService
import com.hfut.schedule.logic.network.api.StuService
import com.hfut.schedule.logic.network.api.SupabaseService
import com.hfut.schedule.logic.network.api.TeachersService
import com.hfut.schedule.logic.network.api.WorkService
import com.hfut.schedule.logic.network.api.XuanChengService
import com.hfut.schedule.logic.network.api.ZJGDBillService
import com.hfut.schedule.logic.network.servicecreator.CommunitySreviceCreator
import com.hfut.schedule.logic.network.servicecreator.DormitoryScoreServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GithubRawServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuHTMLServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.servicecreator.LePaoYunServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.network.servicecreator.MyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.NewsServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneGotoServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneServiceCreator
import com.hfut.schedule.logic.network.servicecreator.QWeatherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.SearchEleServiceCreator
import com.hfut.schedule.logic.network.servicecreator.StuServiceCreator
import com.hfut.schedule.logic.network.servicecreator.SupabaseServiceCreator
import com.hfut.schedule.logic.network.servicecreator.TeacherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.WorkServiceCreator
import com.hfut.schedule.logic.network.servicecreator.XuanChengServiceCreator
import com.hfut.schedule.logic.network.servicecreator.ZJGDBillServiceCreator
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.NetWork
import com.hfut.schedule.logic.util.network.NetWork.launchRequestSimple
import com.hfut.schedule.logic.util.network.PARSE_ERROR_CODE
import com.hfut.schedule.logic.util.network.SimpleStateHolder
import com.hfut.schedule.logic.util.network.supabaseEventDtoToEntity
import com.hfut.schedule.logic.util.network.supabaseEventForkDtoToEntity
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.electric.getPsk
import com.hfut.schedule.ui.screen.home.search.function.loginWeb.getIdentifyID
import com.hfut.schedule.ui.screen.home.search.function.mail.MailResponse
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.transfer.ApplyGrade
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus.HEFEI
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus.XUANCHENG
import com.hfut.schedule.ui.screen.home.search.function.transfer.ChangeMajorInfo
import com.hfut.schedule.ui.screen.home.search.function.transfer.GradeAndRank
import com.hfut.schedule.ui.screen.home.search.function.transfer.MyApplyInfoBean
import com.hfut.schedule.ui.screen.home.search.function.transfer.PlaceAndTime
import com.hfut.schedule.ui.screen.news.home.transferToPostData
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import kotlin.ranges.contains

// 106个函数
class NetWorkViewModel(var webVpn: Boolean) : ViewModel() {
    private val jxglstuJSON = JxglstuJSONServiceCreator.create(JxglstuService::class.java,webVpn)
    private val jxglstuHTML = JxglstuHTMLServiceCreator.create(JxglstuService::class.java,webVpn)
    private val oneGoto = OneGotoServiceCreator.create(LoginService::class.java)
    private val one = OneServiceCreator.create(OneService::class.java)
    private val huixin = ZJGDBillServiceCreator.create(ZJGDBillService::class.java)
    private val xuanquDormitory = DormitoryScoreServiceCreator.create(DormitoryScore::class.java)
    private val lepaoYun = LePaoYunServiceCreator.create(LePaoYunService::class.java)
    private val searchEle = SearchEleServiceCreator.create(FWDTService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)
    private val community = CommunitySreviceCreator.create(CommunityService::class.java)
    private val news = NewsServiceCreator.create(NewsService::class.java)
    private val xuanCheng = XuanChengServiceCreator.create(XuanChengService::class.java)
    private val guaGua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
    private val teacher = TeacherServiceCreator.create(TeachersService::class.java)
    private val myAPI = MyServiceCreator.create(MyService::class.java)
    private val stu = StuServiceCreator.create(StuService::class.java)
    private val qWeather = QWeatherServiceCreator.create(QWeatherService::class.java)
    private val gitee = GiteeServiceCreator.create(GiteeService::class.java)
    private val loginWeb = LoginWebServiceCreator.create(LoginWebsService::class.java)
    private val loginWeb2 = LoginWeb2ServiceCreator.create(LoginWebsService::class.java)
    private val github = GithubRawServiceCreator.create(GithubRawService::class.java)
    private val supabase = SupabaseServiceCreator.create(SupabaseService::class.java)
    private val workSearch = WorkServiceCreator.create(WorkService::class.java)


    var studentId = MutableLiveData<Int>(prefs.getInt("STUDENTID",0))
    var lessonIds = MutableLiveData<List<Int>>()
    var token = MutableLiveData<String>()

    val workSearchResult = SimpleStateHolder<WorkSearchResponse>()
    suspend fun searchWorks(keyword: String?, page: Int = 1,type: Int,campus: Campus) = launchRequestSimple(
        holder = workSearchResult,
        request = { workSearch.search(
            keyword = keyword,
            page = page,
            pageSize = prefs.getString("WorkSearchRequest",MyApplication.PAGE_SIZE.toString())?.toIntOrNull() ?: MyApplication.PAGE_SIZE,
            type = type.let { if(it == 0) null else it },
            token = "yxqqnn1700000" + if(campus == XUANCHENG) "119" else "002"
        ).awaitResponse() },
        transformSuccess = { _, json -> parseWorkResponse(json) },
    )
    fun parseWorkResponse(resp : String): WorkSearchResponse = try {
        // 去掉前缀，提取 JSON 部分
        val jsonStr = resp.removePrefix("var __result = ").removeSuffix(";").trim()
        Gson().fromJson(jsonStr,WorkSearchResponse::class.java)
    } catch (e : Exception) { throw e }

    var supabaseRegResp = MutableLiveData<String?>()
    fun supabaseReg(password: String) = NetWork.makeRequest(supabase.reg(user = SupabaseUserLoginBean(password = password)),supabaseRegResp)

    var supabaseLoginResp = MutableLiveData<String?>()
    fun supabaseLoginWithPassword(password : String) = NetWork.makeRequest(supabase.login(user = SupabaseUserLoginBean(password = password), loginType = "password"),supabaseLoginResp)

    fun supabaseLoginWithRefreshToken(refreshToken : String) = NetWork.makeRequest(supabase.login(user = SupabaseRefreshLoginBean(refreshToken), loginType = "refresh_token",),supabaseLoginResp)

    var supabaseDelResp = MutableLiveData<Boolean?>()
    fun supabaseDel(jwt : String,id : Int) {
        val call = supabase.delEvent(authorization = "Bearer $jwt",id = "eq.$id")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                supabaseDelResp.value = response.isSuccessful
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    var supabaseAddResp = MutableLiveData<Boolean?>()
    fun supabaseAdd(jwt: String,event : SupabaseEventOutput) {
        val call = supabase.addEvent(authorization = "Bearer $jwt",entity = supabaseEventDtoToEntity(event))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                supabaseAddResp.value = response.isSuccessful
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    var supabaseAddCountResp = MutableLiveData<Boolean?>()
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
    var supabaseGetEventsResp = MutableLiveData<String?>()
    fun supabaseGetEvents(jwt: String) = NetWork.makeRequest(supabase.getEvents(authorization = "Bearer $jwt"),supabaseGetEventsResp)

    var supabaseGetEventForkCountResp = SimpleStateHolder<String>()
    suspend fun supabaseGetEventForkCount(jwt: String, eventId: Int) = launchRequestSimple(
        holder = supabaseGetEventForkCountResp,
        request = { supabase.getEventDownloadCount(authorization = "Bearer $jwt", entity = SupabaseEventForkCount(eventId = eventId)).awaitResponse() },
        transformSuccess = { _,body -> body }
    )

    var supabaseGetEventCountResp = SimpleStateHolder<String?>()
    suspend fun supabaseGetEventCount(jwt: String) = launchRequestSimple(
        holder = supabaseGetEventCountResp,
        request = { supabase.getEventCount(authorization = "Bearer $jwt").awaitResponse() },
        transformSuccess = { _,body -> body }
    )

    var supabaseGetEventLatestStatusResp = MutableLiveData<Boolean?>()
    var supabaseGetEventLatestResp = MutableLiveData<String?>()
    fun supabaseGetEventLatest(jwt: String) {

        val call = supabase.getEventLatestTime(authorization = "Bearer $jwt")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    supabaseGetEventLatestResp.value = response.body()?.string()
                    supabaseGetEventLatestStatusResp.value = true
                } else {
                    supabaseGetEventLatestStatusResp.value = false
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }


    // 定制 展示自己上传过的日程
    var supabaseGetMyEventsResp = MutableLiveData<String?>()
    fun supabaseGetMyEvents(jwt: String) = NetWork.makeRequest(supabase.getEvents(authorization = "Bearer $jwt",endTime = null,email = "eq." + getSchoolEmail()),supabaseGetMyEventsResp)


    var supabaseCheckResp = MutableLiveData<Boolean?>()
    fun supabaseCheckJwt(jwt: String) {
        val call = supabase.checkToken(authorization = "Bearer $jwt")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                supabaseCheckResp.value = response.isSuccessful
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    var supabaseUpdateResp = SimpleStateHolder<Boolean>()
    suspend fun updateEvent(jwt: String, id: Int, body : Map<String,Any>) = launchRequestSimple(
        holder = supabaseUpdateResp,
        request = { supabase.updateEvent(authorization = "Bearer $jwt",id = "eq.$id", body = body).awaitResponse() },
        transformSuccess = { _, _ -> true }
    )

    var programList = MutableLiveData<String?>()
    fun getProgramList(campus : Campus) {
        val campusText = when(campus) {
            HEFEI -> "hefei"
            XUANCHENG -> "xuancheng"
        }
        NetWork.makeRequest(myAPI.getProgramList(campusText),programList)
    }

    var programSearchData = MutableLiveData<String?>()
    fun getProgramListInfo(id : Int,campus : Campus) {
        val campusText = when(campus) {
            HEFEI -> "hefei"
            XUANCHENG -> "xuancheng"
        }
        NetWork.makeRequest(myAPI.getProgram(id,campusText),programSearchData)
    }

    fun downloadHoliday() = {
        val call = github.getYearHoliday()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("HOLIDAY", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    val postTransferResponse = MutableLiveData<String?>()
    fun postTransfer(
        cookie: String,
        batchId: String,
        id : String,
        phoneNumber : String,
                     ) = NetWork.makeRequest(
        jxglstuJSON.postTransfer(
            cookie = cookie,
            redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${studentId.value}&batchId=${batchId}&studentId=${studentId.value}".toRequestBody("text/plain".toMediaTypeOrNull()),
            batchId = batchId.toRequestBody("text/plain".toMediaTypeOrNull()),
            id = id.toRequestBody("text/plain".toMediaTypeOrNull()),
            studentID = studentId.value.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            telephone = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        ), postTransferResponse)

    val formCookie = MutableLiveData<String?>()
    fun getFormCookie(
        cookie: String,
        batchId: String,
        id : String,
    )  {
        val call = jxglstuHTML.getFormCookie(
            cookie = cookie,
            id = id,
            studentId = studentId.value.toString(),
            redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${studentId.value}&batchId=${batchId}&studentId=${studentId.value}",
            batchId = batchId
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.headers()["Set-Cookie"].toString().let {
                    formCookie.value = it.split(";")[0]
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    val cancelTransferResponse = MutableLiveData<Boolean?>()
    fun cancelTransfer(
        cookie: String,
        batchId: String,
        id : String,
    ) {
        val call = jxglstuJSON.cancelTransfer(
                cookie = cookie,
                redirectUrl = "/for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/${studentId.value}&batchId=${batchId}&studentId=${studentId.value}",
                batchId = batchId,
                studentId = studentId.value.toString(),
                applyId = id
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                cancelTransferResponse.value = response.code() == 302
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                cancelTransferResponse.value = false
            }
        })
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
    @SuppressLint("SuspiciousIndentation")
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
    @SuppressLint("SuspiciousIndentation")
    fun getSelectCourse(cookie: String) {
        val call = prefs.getString("Username","2023XXXXXX")?.let {
            jxglstuJSON.getSelectCourse(
                it.substring(2,4),
                studentId.value.toString(), cookie)
        }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        selectCourseData.value = response.body()?.string()
                    } else {
                        Log.e("Error", "Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    val selectCourseInfoData = SimpleStateHolder<List<SelectCourseInfo>>()
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
    @SuppressLint("SuspiciousIndentation")
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
    @SuppressLint("SuspiciousIndentation")
    fun getRequestID(cookie: String,lessonId : String,courseId : String,type : String) {
        val call = jxglstuJSON.getRequestID(studentId.value.toString(),lessonId,courseId,cookie,type)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                requestIdData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    val selectedData = MutableLiveData<String?>()
    @SuppressLint("SuspiciousIndentation")
    fun getSelectedCourse(cookie: String,courseId : String) {
        val call = jxglstuJSON.getSelectedCourse(studentId.value.toString(),courseId,cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                selectedData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    val selectResultData = MutableLiveData<String?>()
    @SuppressLint("SuspiciousIndentation")
    fun postSelect(cookie: String,requestId : String) {
        val call = jxglstuJSON.postSelect(studentId.value.toString(), requestId,cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                selectResultData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

// 转专业 ////////////////////////////////////////////////////////////////////////////////////////////////
    val transferData = SimpleStateHolder<TransferResponse>()
    suspend fun getTransfer(cookie: String,batchId: String) = launchRequestSimple(
        holder = transferData,
        request = { jxglstuJSON.getTransfer(cookie,true, batchId, studentId.value ?: 0).awaitResponse() },
        transformSuccess = { _,json -> parseTransfer(json) }
    )
    private fun parseTransfer(json : String) : TransferResponse = try {
        Gson().fromJson(json, TransferResponse::class.java)
    } catch (e : Exception) { throw e }

    val transferListData = SimpleStateHolder<List<ChangeMajorInfo>>()
    suspend fun getTransferList(cookie: String) = launchRequestSimple(
        holder = transferListData,
        request = { jxglstuHTML.getTransferList(cookie,studentId.value ?: 0).awaitResponse() },
        transformSuccess = { _,html -> parseTransferList(html) }
    )

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


    val myApplyData = SimpleStateHolder<MyApplyResponse>()
    suspend fun getMyApply(cookie: String,batchId: String) = launchRequestSimple(
        holder = myApplyData,
        request = { jxglstuJSON.getMyTransfer(cookie,batchId,studentId.value ?: 0).awaitResponse() },
        transformSuccess = { _,json -> parseMyApply(json) }
    )

    private fun parseMyApply(json: String) : MyApplyResponse = try {
        Gson().fromJson(json, MyApplyResponse::class.java)
    } catch (e : Exception) { throw e }

    val myApplyInfoData = SimpleStateHolder<MyApplyInfoBean>()
    suspend fun getMyApplyInfo(cookie: String, listId: Int) = launchRequestSimple(
        holder = myApplyInfoData,
        request = { jxglstuHTML.getMyTransferInfo(cookie,listId,studentId.value ?: 0).awaitResponse() },
        transformSuccess = { _,html -> parseMyApplyGradeInfo(html) }
    )

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
    val newsResult = SimpleStateHolder<List<NewsResponse>>()
    suspend fun searchNews(title : String,page: Int = 1) = launchRequestSimple(
        holder = newsResult,
        request = { news.searchNews(Encrypt.encodeToBase64(title),page).awaitResponse() },
        transformSuccess = { _,html -> parseNews(html) }
    )

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

    val newsXuanChengResult = SimpleStateHolder<List<XuanquNewsItem>>()
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

    suspend fun getXuanChengNews(page: Int) = launchRequestSimple(
        holder = newsXuanChengResult,
        request = { xuanCheng.getNotications(page = page.let { if(it <= 1)  ""  else  it.toString()  }).awaitResponse() },
        transformSuccess = { _,html -> parseNewsXuanCheng(html) }
    )

    private fun parseNewsXuanCheng(html : String) : List<XuanquNewsItem> = try {
        val document = Jsoup.parse(html)
        document.select("ul.news_list > li").map { element ->
            val titleElement = element.selectFirst("span.news_title a")
            val title = titleElement?.attr("title") ?: "未知标题"
            val url = titleElement?.attr("href") ?: "未知URL"
            val date = element.selectFirst("span.news_meta")?.text() ?: "未知日期"

            XuanquNewsItem(title, date, url)
        }
    } catch (e : Exception) { throw e }
//  ////////////////////////////////////////////////////////////////////////////////////////////////
    fun GotoCommunity(cookie : String) {

        val call = login.loginGoTo(service = LoginType.COMMUNITY.service,cookie = cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val LoginCommunityData = MutableLiveData<String?>()
    fun LoginCommunity(ticket : String) {

        val call = community.Login(ticket)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                LoginCommunityData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val jxglstuGradeData = MutableLiveData<String?>()
    fun getGrade(cookie: String,semester: Int?) {
        val call = jxglstuJSON.getGrade(cookie,studentId.value.toString(), semester)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                saveString("grade",body )
                jxglstuGradeData.value = body
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun Jxglstulogin(cookie : String) {

        val call = jxglstuJSON.jxglstulogin(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val bizTypeIdResponse = MutableLiveData<String?>()
    fun getBizTypeId(cookie: String) {
        studentId.value?.let { NetWork.makeRequest(
            call = jxglstuHTML.getBizTypeId(cookie,it),
            liveData = bizTypeIdResponse
        ) }
    }

    fun getStudentId(cookie : String) {

        val call = jxglstuJSON.getStudentId(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.headers()["Location"].toString().contains("/eams5-student/for-std/course-table/info/")) {
                    studentId.value = response.headers()["Location"].toString()
                        .substringAfter("/eams5-student/for-std/course-table/info/").toInt()
                    saveInt("STUDENTID",studentId.value ?: 0)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getLessonIds(cookie : String, bizTypeId : Int,studentid : String) {
        //bizTypeId不是年级数！  //dataId为学生ID  //semesterId为学期Id，例如23-24第一学期为234
        val call =  jxglstuJSON.getLessonIds(
            cookie,
            bizTypeId.toString(),
            SemseterParser.getSemseter().toString(),
            studentid
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                if (json != null) {
                    try {
                        val id = Gson().fromJson(json, lessonResponse::class.java)
                        lessonIds.value = id.lessonIds
                    } catch (e : Exception) { }
                    saveString("courses",json)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val datumData = MutableLiveData<String?>()
    fun getDatum(cookie : String,lessonid: JsonObject) {

        val lessonIdsArray = JsonArray()
        lessonIds.value?.forEach {lessonIdsArray.add(JsonPrimitive(it))}

        val call = jxglstuJSON.getDatum(cookie,lessonid)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                datumData.value = body
                if (body != null && body.contains("result")) {
                    saveString("json", body)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getInfo(cookie : String) {

        val call = jxglstuHTML.getInfo(cookie,studentId.value.toString())

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

    val ProgramData = MutableLiveData<String?>()
    fun getProgram(cookie: String) {
        val call = jxglstuJSON.getProgram(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                saveString("program", body)
                ProgramData.value = body

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val ProgramCompletionData = MutableLiveData<String?>()
    fun getProgramCompletion(cookie: String) {
        val call = jxglstuJSON.getProgramCompletion(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                ProgramCompletionData.value = body
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val programPerformanceData = MutableLiveData<String?>()
    fun getProgramPerformance(cookie: String) {
        val call = studentId.value?.let { jxglstuJSON.getProgramPerformance(cookie, it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val body = response.body()?.string()
                    programPerformanceData.value = body
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    val teacherSearchData = SimpleStateHolder<TeacherResponse>()
    suspend fun searchTeacher(name: String = "", direction: String = "") = launchRequestSimple(
        holder = teacherSearchData,
        request = { teacher.searchTeacher(name=name, direction = direction, size = prefs.getString("TeacherSearchRequest",MyApplication.PAGE_SIZE.toString()) ?: MyApplication.PAGE_SIZE.toString() ).awaitResponse() },
        transformSuccess = { _,json -> parseTeacherSearch(json) }
    )

    private fun parseTeacherSearch(json : String) : TeacherResponse = try {
        Gson().fromJson(json, TeacherResponse::class.java)
    } catch (e : Exception) { throw e }

    val courseData = MutableLiveData<String?>()
    val courseRsponseData = MutableLiveData<String?>()
    fun searchCourse(cookie: String, className : String?,courseName : String?, semester : Int,courseId : String?) {
        val call = jxglstuJSON.searchCourse(cookie,studentId.value.toString(),semester,className,"1,${prefs.getString("CourseSearchRequest",MyApplication.PAGE_SIZE.toString()) ?: MyApplication.PAGE_SIZE}",courseName,courseId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                courseData.value = response.code().toString()
                courseRsponseData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val surveyListData = SimpleStateHolder<List<lessonSurveyTasks>>()
    suspend fun getSurveyList(cookie: String, semester : Int) = launchRequestSimple(
        holder = surveyListData,
        request = { jxglstuJSON.getSurveyList(cookie,studentId.value.toString(),semester).awaitResponse() },
        transformSuccess = { _,json -> parseSurveyList(json) }
    )
    private fun parseSurveyList(json : String) : List<lessonSurveyTasks> = try {
        val list = mutableListOf<lessonSurveyTasks>()
        val result = Gson().fromJson(json, SurveyTeacherResponse::class.java).forStdLessonSurveySearchVms
        for(i in result.indices) {
            val teacherList = result[i].lessonSurveyTasks
            for(j in teacherList.indices) {
                list.add(teacherList[j])
            }
        }
        list
    } catch (e : Exception) { throw e }

    val surveyData = MutableLiveData<String?>()
    fun getSurvey(cookie: String, id : String) {
        val call = jxglstuJSON.getSurveyInfo(cookie,id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                surveyData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getSurveyToken(cookie: String, id : String) {
        val call = jxglstuJSON.getSurveyToken(cookie,id,"/for-std/lesson-survey/semester-index/${studentId.value}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                 saveString("SurveyCookie", response.headers().toString().substringAfter("Set-Cookie:").substringBefore(";"))
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

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

    fun getPhoto(cookie : String){
        val call = jxglstuJSON.getPhoto(cookie,studentId.value.toString())

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
                } catch (e : Exception) {

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun OneGoto(cookie : String)  {// 创建一个Call对象，用于发送异步请求

        val call = oneGoto.OneGoto(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun OneGotoCard(cookie : String)  {

        val call = oneGoto.OneGotoCard(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val BillsData = MutableLiveData<String?>()
    fun CardGet(auth : String,page : Int) {// 创建一个Call对象，用于发送异步请求

        val size = prefs.getString("CardRequest","15")
        val call = size?.let { huixin.Cardget(auth,page, it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    BillsData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }


    }

    val CardData = MutableLiveData<String?>()
    fun getyue(auth : String) {
        val call = huixin.getYue(auth)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                CardData.value = body
                saveString("cardyue",body )
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }

    val infoValue = MutableLiveData<String?>()
    val showerData = MutableLiveData<String?>()
    fun getFee(auth: String,type : FeeType,level : String? = null,room : String? = null,phoneNumber : String? = null) {

        val feeitemid = type.code.toString()
        val levels = when(type) {
            WEB -> "0"
            ELECTRIC -> null
            SHOWER -> "1"
        }
        val rooms = when(type) {
            WEB -> null
            ELECTRIC -> room
            SHOWER -> null
        }
        val phoneNumbers = when(type) {
            WEB -> null
            ELECTRIC -> null
            SHOWER -> phoneNumber
        }
        val call = huixin.getFee(auth, typeId = feeitemid, room = rooms, level = levels, phoneNumber = phoneNumbers)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                when(type) {
                    WEB -> infoValue.value = responseBody
                    ELECTRIC ->  ElectricData.value = responseBody
                    SHOWER -> showerData.value = responseBody
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val guaguaUserInfo = MutableLiveData<String?>()
    fun getGuaGuaUserInfo() {
        val loginCode = prefs.getString("loginCode","") ?: ""
        val phoneNumber = prefs.getString("PHONENUM","") ?: ""
        val call = phoneNumber.let { loginCode.let { it1 -> guaGua.getUserInfo(it, it1) } }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                guaguaUserInfo.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val orderIdData = SimpleStateHolder<String>()
    suspend fun payStep1(auth: String,json: String,pay : Float,type: FeeType) = launchRequestSimple(
        holder = orderIdData,
        request = {
            huixin.pay(
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
        transformSuccess = { _,json -> parseHuixinPayStep1(json) }
    )
    private fun parseHuixinPayStep1(result : String) : String = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, PayStep1Response::class.java).data.orderid
        } else {
            throw Exception("Step1失败 终止支付")
        }
    } catch (e : Exception) { throw e }

    val uuIdData = SimpleStateHolder<Map<String, String>>()
    suspend fun payStep2(auth: String,orderId : String,type : FeeType) = launchRequestSimple(
        holder = uuIdData,
        request = {
            huixin.pay(
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
        transformSuccess = { _,json -> parseHuixinPayStep2(json) }
    )

    private fun parseHuixinPayStep2(result : String) : Map<String, String> = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, PayStep2Response::class.java).data.passwordMap
        } else {
            throw Exception("Step2失败 终止支付")
        }
    } catch (e : Exception) { throw e }

    val payResultData = SimpleStateHolder<String>()
    suspend fun payStep3(auth: String,orderId : String,password : String,uuid : String,type: FeeType) = launchRequestSimple(
        holder = payResultData,
        request = {
            huixin.pay(
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
        transformSuccess = { _,json -> parseHuixinPayStep3(json)}
    )
    private fun parseHuixinPayStep3(result : String) : String = try {
        if(result.contains("success")) {
            Gson().fromJson(result, PayStep3Response::class.java).msg
        } else {
            throw Exception("支付失败")
        }
    } catch (e : Exception) { throw e }


    fun changeLimit(auth: String,json: JsonObject) {

        val call = huixin.changeLimit(auth,json)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("changeResult", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    var huixinRangeResult = SimpleStateHolder<Float>()
    suspend fun searchDate(auth : String, timeFrom : String, timeTo : String) = launchRequestSimple(
        holder = huixinRangeResult,
        request = { huixin.searchDate(auth,timeFrom,timeTo).awaitResponse() },
        transformSuccess = { _, json -> parseHuixinRange(json) }
    )

    private fun parseHuixinRange(result : String) : Float = try {
        if(result.contains("操作成功")) {
            val data = Gson().fromJson(result, BillRangeResponse::class.java)
            data.data.expenses / 100
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    val huixinSearchBillsResult = SimpleStateHolder<BillDatas>()
    suspend fun searchBills(auth : String, info: String,page : Int) = launchRequestSimple(
        holder = huixinSearchBillsResult,
        request = { huixin.searchBills(auth,info,page, prefs.getString("CardRequest","30") ?: MyApplication.PAGE_SIZE.toString()).awaitResponse() },
        transformSuccess = { _, json -> parseHuixinSearchBills(json) }
    )

    private fun parseHuixinSearchBills(result : String) : BillDatas = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result,BillResponse::class.java).data
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    val huixinMonthBillResult = SimpleStateHolder<List<BillMonth>>()
    suspend fun getMonthBills(auth : String, dateStr: String) = launchRequestSimple(
        holder = huixinMonthBillResult,
        request = { huixin.getMonthYue(auth,dateStr).awaitResponse() },
        transformSuccess = { _, json -> parseHuixinMonthBills(json) }
    )
    private fun parseHuixinMonthBills(json : String) : List<BillMonth> = try {
        if(json.contains("操作成功")) {
            val data = Gson().fromJson(json, BillMonthResponse::class.java)
            val bill = data.data
            bill.map { (date,balance) -> BillMonth(date, balance) }
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw e }

    fun getToken()  {

        val codehttp = prefs.getString("code", "")
        var code = codehttp
        if (code != null) { code = code.substringAfter("=") }
        if (code != null) { code = code.substringBefore("]") }
        val http = codehttp?.substringAfter("[")?.substringBefore("]")


        val call = http?.let { code?.let { it1 -> one.getToken(it, it1) } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val json = response.body()?.string()
                    try {
                        val data = Gson().fromJson(json, getTokenResponse::class.java)
                        if (data.msg == "success") {
                            token.value = data.data.access_token
                            saveString("bearer", data.data.access_token)
                        }
                    } catch (_: Exception) {}
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }


    }

    val ElectricData = MutableLiveData<String?>()
    fun searchEle(jsondata : String) {
        val call = searchEle.searchEle(jsondata,"synjones.onecard.query.elec.roominfo",true)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                ElectricData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ElectricData.value = "查询失败,是否连接了hfut-wlan?"
                t.printStackTrace()
            }
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

    fun searchEmptyRoom(building_code : String,token : String)  {

        val call = one.searchEmptyRoom(building_code, "Bearer $token")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("emptyjson", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val mailData = SimpleStateHolder<MailResponse?>()
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

    val PayData = MutableLiveData<String?>()
    fun getPay()  {

        val call = prefs.getString("Username","")?.let { one.getPay(it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    PayData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    val XuanquData = MutableLiveData<String>()
    fun SearchXuanqu(code : String) {

        val call = xuanquDormitory.SearchXuanqu(code)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                XuanquData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun LePaoYunHome(Yuntoken : String) {

        val call = lepaoYun.getLePaoYunHome(Yuntoken)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("LePaoYun", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val FailRateData = MutableLiveData<String>()
    fun SearchFailRate(CommuityTOKEN : String,name: String,page : String) {
        val size = prefs.getString("FailRateRequest","15")
        //size?.let { Log.d("size", it) }
        val call = CommuityTOKEN?.let { size?.let { it1 -> community.getFailRate(it,name,page, it1) } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    FailRateData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    val ExamData = MutableLiveData<String?>()
    val ExamCodeData = MutableLiveData<Int>()
    fun Exam(CommuityTOKEN: String) {

        val call = CommuityTOKEN?.let { community.getExam(it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val responses = response.body()?.string()
                    saveString("Exam", responses)
                    ExamData.value = responses
                    ExamCodeData.value = response.code()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    ExamData.value = "错误"
                    t.printStackTrace()
                }
            })
        }
    }

    val examCode = MutableLiveData<Int>()
    fun getExamJXGLSTU(cookie: String) {
        val call = jxglstuJSON.getExam(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val code = response.code()
                examCode.value = code
                if(code == 200) {
                    saveString("examJXGLSTU", response.body()?.string())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    var GradeData = MutableLiveData<String?>()
    fun getGrade(CommuityTOKEN: String,year : String,term : String) = NetWork.makeRequest(community.getGrade(CommuityTOKEN,year,term),GradeData)
    var avgData = MutableLiveData<String?>()
    fun getAvgGrade(CommuityTOKEN: String) = NetWork.makeRequest(community.getAvgGrade(CommuityTOKEN),avgData)

    var allAvgData = MutableLiveData<String?>()

    fun getAllAvgGrade(CommuityTOKEN: String) = NetWork.makeRequest(community.getAllAvgGrade(CommuityTOKEN),allAvgData)

    val libraryData = MutableLiveData<String?>()
    fun SearchBooks(CommuityTOKEN: String,name: String,page: Int) {

        val size = prefs.getString("BookRequest","15")
      //  size?.let { Log.d("size", it) }
        val call = CommuityTOKEN.let { size?.let { it1 -> community.searchBooks(it,name,page.toString(), it1) } }
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                libraryData.value = response.body()?.string()
                saveString("Library", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                libraryData.value = "错误"
                t.printStackTrace()
            }
        })
    }

    val bookPositionData = MutableLiveData<String?>()
    fun getBookPosition(token: String,callNo: String) = NetWork.makeRequest(community.getBookPosition(token,callNo),bookPositionData)

    fun GetCourse(CommuityTOKEN : String,studentId: String? = null) {

        val call = CommuityTOKEN?.let { community.getCourse(it,studentId) }

        if (call != null) {
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
    }

    fun openFriend(CommuityTOKEN : String) {

        val call = CommuityTOKEN?.let { community.switchShare(it, CommunityService.RequestJson(1)) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    val applyResponseMsg = MutableLiveData<String>()
    fun addApply(CommuityTOKEN : String,username : String) {

        val call = community.applyAdd(CommuityTOKEN,CommunityService.RequestJsonApply(username))

        NetWork.makeRequest(call,applyResponseMsg)

    }

    val applyData = MutableLiveData<String>()
    fun getApplying(CommuityTOKEN : String) {
        val size = prefs.getString("CardRequest","15")
        val call = size?.let { community.getApplyingList(CommuityTOKEN, it) }

        call?.let { NetWork.makeRequest(it,applyData) }

    }

    val booksChipData = MutableLiveData<String?>()

    fun communityBooks(token : String,type : LibraryItems,page : Int = 1) {
        val size = 500
        NetWork.makeRequest(
            when(type) {
                LibraryItems.OVERDUE -> community.getOverDueBook(token, page.toString(),size.toString())
                LibraryItems.HISTORY -> community.getHistoryBook(token, page.toString(),size.toString())
                LibraryItems.BORROWED -> community.getBorrowedBook(token, page.toString(),size.toString())
            },
            booksChipData
        )
    }

    fun getToday(CommuityTOKEN : String) {

        val call = CommuityTOKEN?.let { community.getToday(it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    saveString("TodayNotice", response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    fun getFriends(CommuityTOKEN : String) {

        val call = CommuityTOKEN?.let { community.getFriends(it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    saveString("feiends", response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    fun checkApplying(CommuityTOKEN : String,id : String,isOk : Boolean) {

        val call = CommuityTOKEN?.let { community.checkApplying(it,CommunityService.RequestApplyingJson(id,if(isOk) 1 else 0)) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    var lessonIdsNext = MutableLiveData<List<Int>>()
    fun getLessonIdsNext(cookie : String, bizTypeId : Int,studentid : String) {
        val call = (SemseterParser.getSemseter()?.plus(20)).toString()
            ?.let { jxglstuJSON.getLessonIds(cookie,bizTypeId.toString(), it,studentid) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val json = response.body()?.string()
                    if (json != null) {
                        try {
                            val id = Gson().fromJson(json, lessonResponse::class.java)
                            saveString("coursesNext",json)
                            lessonIdsNext.value = id.lessonIds
                        } catch (_ : Exception) {}
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    fun getDatumNext(cookie : String,lessonid: JsonObject) {

        val lessonIdsArray = JsonArray()
        lessonIds.value?.forEach {lessonIdsArray.add(JsonPrimitive(it))}

        val call = jxglstuJSON.getDatum(cookie,lessonid)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
            //    datumDataNext.value = body
                saveString("jsonNext", body)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val weatherWarningData = MutableLiveData<String?>()
    fun getWeatherWarn() = NetWork.makeRequest(qWeather.getWeatherWarn(),weatherWarningData)

    val qWeatherResult = SimpleStateHolder<QWeatherNowBean>()
    suspend fun getWeather() = launchRequestSimple(
        holder = qWeatherResult,
        request = { qWeather.getWeather().awaitResponse() },
        transformSuccess = { _, json -> parseQweatherNow(json) }
    )

    private fun parseQweatherNow(json : String) : QWeatherNowBean = try {
        if(json.contains("200"))
            Gson().fromJson(json, QWeatherResponse::class.java).now
        else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    val goToStuResponse = MutableLiveData<String?>()
    val stuTicket = MutableLiveData<String?>(null)
    fun loginToStu(cookie : String) {
        val call = login.loginGoTo(service = LoginType.STU.service, cookie = cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val statusCode = response.code()
                if(statusCode == 302) {
                    goToStuResponse.value = response.headers()["Location"]
                } else {
//                    Log.d(statusCode.toString(),"登陆失败")
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
                val statusCode = response.code()
                if(statusCode == 200) {
//                    Log.d("成功",response.headers()["Set-Cookie"].toString())
                    loginStuResponse.value = response.headers()["Set-Cookie"]
                } else {
//                    Log.d("登失败",response.headers().toString())
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val stuInfoResponse = MutableLiveData<String?>()
    fun getStuInfo(cookie: String) = NetWork.makeRequest(
        call = stu.getStudentInfo(cookie),
        liveData = stuInfoResponse
    )

    fun getUpdate() {

        val call = gitee.getUpdate()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("versions",response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val resultValue = MutableLiveData<String?>()
    fun loginWeb() {

        val call = getPersonInfo().username?.let { getIdentifyID()?.let { it1 -> loginWeb.loginWeb(it, it1,"宣州Login") } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    resultValue.value = response?.body()?.string()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    resultValue.value = "Error"
                }
            })
        }
    }

    val result2Value = MutableLiveData<String?>()
    fun loginWeb2() {

        val call = getPersonInfo().username?.let { getIdentifyID()?.let { it1 -> loginWeb2.loginWeb(it, it1,"宣州Login") } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    result2Value.value = response?.body()?.string()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    result2Value.value = "Error"
                }
            })
        }
    }

    fun logoutWeb() {
        val call =  loginWeb.logoutWeb()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                resultValue.value = response.body()?.string()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                resultValue.value = "Error"
            }
        })
    }
    val infoWebValue = MutableLiveData<String?>()

    fun getWebInfo() {
        val call =  loginWeb.getInfo()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                infoWebValue.value = response.body()?.string()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                infoWebValue.value = "Error"
            }
        })
    }

    fun getWebInfo2() {
        val call =  loginWeb2.getInfo()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                infoWebValue.value = response.body()?.string()
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                infoWebValue.value = "Error"
            }
        })
    }
}


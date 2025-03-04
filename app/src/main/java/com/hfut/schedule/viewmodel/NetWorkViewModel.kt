package com.hfut.schedule.viewmodel

//import com.hfut.schedule.logic.network.ServiceCreator.Login.OneGetNewTicketServiceCreator.client
import android.annotation.SuppressLint
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.logic.enums.LibraryItems
import com.hfut.schedule.logic.beans.jxglstu.lessonResponse
import com.hfut.schedule.logic.beans.one.BorrowBooksResponse
import com.hfut.schedule.logic.beans.one.SubBooksResponse
import com.hfut.schedule.logic.beans.one.getTokenResponse
import com.hfut.schedule.logic.beans.zjgd.FeeType
import com.hfut.schedule.logic.beans.zjgd.FeeType.*
import com.hfut.schedule.logic.enums.LoginType
import com.hfut.schedule.logic.network.NetWork
import com.hfut.schedule.logic.network.servicecreator.CommunitySreviceCreator
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuHTMLServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuSurveyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.LePaoYunServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.servicecreator.NewsServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneGotoServiceCreator
import com.hfut.schedule.logic.network.servicecreator.SearchEleServiceCreator
import com.hfut.schedule.logic.network.servicecreator.ServerServiceCreator
import com.hfut.schedule.logic.network.servicecreator.DormitoryScoreServiceCreator
import com.hfut.schedule.logic.network.servicecreator.TeacherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.XuanChengServiceCreator
import com.hfut.schedule.logic.network.servicecreator.ZJGDBillServiceCreator
import com.hfut.schedule.logic.network.api.CommunityService
import com.hfut.schedule.logic.network.api.FWDTService
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LePaoYunService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.NewsService
import com.hfut.schedule.logic.network.api.OneService
import com.hfut.schedule.logic.network.api.ServerService
import com.hfut.schedule.logic.network.api.DormitoryScore
import com.hfut.schedule.logic.network.api.GithubService
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.QWeatherService
import com.hfut.schedule.logic.network.api.StuService
import com.hfut.schedule.logic.network.api.TeachersService
import com.hfut.schedule.logic.network.api.XuanChengService
import com.hfut.schedule.logic.network.api.ZJGDBillService
import com.hfut.schedule.logic.network.servicecreator.GithubServiceCreator
import com.hfut.schedule.logic.network.servicecreator.MyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.QWeatherServiceCreator
import com.hfut.schedule.logic.network.servicecreator.StuServiceCreator
import com.hfut.schedule.logic.utils.parse.Encrypt
import com.hfut.schedule.logic.utils.parse.Semseter
import com.hfut.schedule.logic.utils.data.SharePrefs.saveString
import com.hfut.schedule.logic.utils.data.SharePrefs.saveInt
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.cube.items.subitems.getUserInfo
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.activity.news.main.transferToPostData
import com.hfut.schedule.ui.activity.home.search.functions.transferMajor.CampusId
import com.hfut.schedule.ui.activity.home.search.functions.transferMajor.CampusId.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NetWorkViewModel(var webVpn: Boolean) : ViewModel() {

    // 通用网络请求处理函数
    private fun launchRequest(
        flow: MutableStateFlow<String?>? = null,
        sharedFlow: MutableSharedFlow<String?>? = null,
        request: suspend () -> Response<ResponseBody>
    ) {
        viewModelScope.launch {
            val result: String? = try {
                val response = withContext(Dispatchers.IO) { request() } // 网络请求
                if (response.isSuccessful) {
                    response.body()?.string() // 返回 JSON 字符串
                } else {
                    null // 请求失败时返回 null
                }
            } catch (e: Exception) {
                null
            }

            flow?.value = result       // 更新 StateFlow
            sharedFlow?.emit(result)   // SharedFlow 发送数据
        }
    }

    private val JxglstuJSON = JxglstuJSONServiceCreator.create(JxglstuService::class.java,webVpn)
    private val JxglstuHTML = JxglstuHTMLServiceCreator.create(JxglstuService::class.java,webVpn)
    private val OneGoto = OneGotoServiceCreator.create(LoginService::class.java)
    private val One = OneServiceCreator.create(OneService::class.java)
    private val ZJGDBill = ZJGDBillServiceCreator.create(ZJGDBillService::class.java)
    private val Xuanqu = DormitoryScoreServiceCreator.create(DormitoryScore::class.java)
    private val LePaoYun = LePaoYunServiceCreator.create(LePaoYunService::class.java)
    private val searchEle = SearchEleServiceCreator.create(FWDTService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)
   /// private val JwglappLogin = JwglappServiceCreator.create(JwglappService::class.java)
    private val Community = CommunitySreviceCreator.create(CommunityService::class.java)
  //  private val Jwglapp = JwglappServiceCreator.create(JwglappService::class.java)
    private val News = NewsServiceCreator.create(NewsService::class.java)
    private val xuanCheng = XuanChengServiceCreator.create(XuanChengService::class.java)
    private val JxglstuSurvey = JxglstuSurveyServiceCreator.create(JxglstuService::class.java,webVpn)
    private val server = ServerServiceCreator.create(ServerService::class.java)
    private val guagua = GuaGuaServiceCreator.create(GuaGuaService::class.java)
    private val teacher = TeacherServiceCreator.create(TeachersService::class.java)
    private val MyAPI = MyServiceCreator.create(MyService::class.java)
    private val stu = StuServiceCreator.create(StuService::class.java)
    private val qWeather = QWeatherServiceCreator.create(QWeatherService::class.java)


    var studentId = MutableLiveData<Int>(prefs.getInt("STUDENTID",0))
    var lessonIds = MutableLiveData<List<Int>>()
    var token = MutableLiveData<String>()



    var programList = MutableLiveData<String?>()
    fun getProgramList(campus : CampusId) {
        val campusText = when(campus) {
            HEFEI -> "hefei"
            XUANCHENG -> "xuancheng"
        }
        NetWork.makeRequest(MyAPI.getProgramList(campusText),programList)
    }

    var programSearchData = MutableLiveData<String?>()
    fun getProgramListInfo(id : Int,campus : CampusId) {
        val campusText = when(campus) {
            HEFEI -> "hefei"
            XUANCHENG -> "xuancheng"
        }
        NetWork.makeRequest(MyAPI.getProgram(id,campusText),programSearchData)
    }



    val postTransferResponse = MutableLiveData<String?>()
    fun postTransfer(
        cookie: String,
        batchId: String,
        id : String,
        phoneNumber : String,
                     ) = NetWork.makeRequest(
        JxglstuJSON.postTransfer(
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
        val call = JxglstuHTML.getFormCookie(
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
        val call = JxglstuJSON.cancelTransfer(
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
        val data = getUserInfo()
        val call = server.postUse(
            dateTime = data.dateTime,
            deviceName = data.deviceName ?: "空",
            appVersion = data.appVersionName,
            systemVersion = data.systemVersion,
            studentID = data.studentID ?: "空",
            user = data.name ?: "空")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun feedBack(info : String,contact : String?) {
        val data = getUserInfo()
        val time = data.dateTime
        val version = data.appVersionName
        val name = data.name

        val call = server.feedBack(
            dateTime = time,
            appVersion = version,
            user = name ?: "空",
            info = info,
            contact = contact)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val verifyData = MutableLiveData<String?>()
    @SuppressLint("SuspiciousIndentation")
    fun verify(cookie: String) {
        val call = JxglstuJSON.verify(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    verifyData.value = response.code().toString()
                } else {
                    verifyData.value = response.code().toString()
                    Log.e("Error", "Response code: ${response.code()}")
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
            JxglstuJSON.getSelectCourse(
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

    val selectCourseInfoData = MutableLiveData<String?>()
    @SuppressLint("SuspiciousIndentation")
    fun getSelectCourseInfo(cookie: String,id : Int) {
        val call = JxglstuJSON.getSelectCourseInfo(id,cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    selectCourseInfoData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    val stdCountData = MutableLiveData<String?>()
    @SuppressLint("SuspiciousIndentation")
    fun getSCount(cookie: String,id : Int) {
        val call = JxglstuJSON.getCount(id,cookie)

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
        val call = JxglstuJSON.getRequestID(studentId.value.toString(),lessonId,courseId,cookie,type)

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
        val call = JxglstuJSON.getSelectedCourse(studentId.value.toString(),courseId,cookie)

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
        val call = JxglstuJSON.postSelect(studentId.value.toString(), requestId,cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                selectResultData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    val transferData = MutableLiveData<String?>()
    fun getTransfer(cookie: String,batchId: String) {

        val call = studentId.value?.let { JxglstuJSON.getTransfer(cookie,true, batchId, it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    transferData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    val transferListData = MutableLiveData<String?>()
    fun getTransferList(cookie: String) = studentId.value?.let { NetWork.makeRequest(JxglstuHTML.getTransferList(cookie,it),transferListData) }


    val myApplyData = MutableLiveData<String?>()
    fun getMyApply(cookie: String,batchId: String) {

        val call = studentId.value?.let { JxglstuJSON.getMyTransfer(cookie,batchId,it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    myApplyData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    val myApplyInfoData = MutableLiveData<String?>()
    fun getMyApplyInfo(cookie: String,listId: Int) {

        val call = studentId.value?.let { JxglstuHTML.getMyTransferInfo(cookie,listId,it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    myApplyInfoData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }


    val NewsData = MutableLiveData<String?>()
    fun searchNews(title : String,page: Int = 1) {

        val call = News.searchNews(title,page)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                NewsData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    val NewsXuanChengData = MutableLiveData<String?>()
    fun searchXuanChengNews(title : String, page: Int = 1) {

        val postData = transferToPostData(title, page)
        val call = xuanCheng.searchNotications(postData)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                NewsXuanChengData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getXuanChengNews(page: Int) {

        val pageText = if(page <= 1) {
            ""
        } else {
            page.toString()
        }

        val call = xuanCheng.getNotications(page = pageText)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                NewsXuanChengData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    fun GotoCommunity(cookie : String) {

        val call = login.loginGoTo(service = LoginType.COMMUNITY.service,cookie = cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val LoginCommunityData = MutableLiveData<String?>()
    fun LoginCommunity(ticket : String) {

        val call = Community.Login(ticket)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                LoginCommunityData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val jxglstuGradeData = MutableLiveData<String?>()

    fun getGrade(cookie: String,semester: Int?) {
        val call = JxglstuJSON.getGrade(cookie,studentId.value.toString(), semester)

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

        val call = JxglstuJSON.jxglstulogin(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val bizTypeIdResponse = MutableLiveData<String?>()
    fun getBizTypeId(cookie: String) {
        studentId.value?.let { NetWork.makeRequest(
            call = JxglstuHTML.getBizTypeId(cookie,it),
            liveData = bizTypeIdResponse
        ) }
    }

    fun getStudentId(cookie : String) {

        val call = JxglstuJSON.getStudentId(cookie)

        //Log.d("web",webVpn.toString())
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
        val call =  JxglstuJSON.getLessonIds(
            cookie,
            bizTypeId.toString(),
            Semseter.getSemseterFromCloud().toString(),
            studentid
        )

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val json = response.body()?.string()
                    if (json != null) {
                        val id = Gson().fromJson(json, lessonResponse::class.java)
                        saveString("courses",json)
                        lessonIds.value = id.lessonIds
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    val datumData = MutableLiveData<String?>()
    fun getDatum(cookie : String,lessonid: JsonObject) {

        val lessonIdsArray = JsonArray()
        lessonIds.value?.forEach {lessonIdsArray.add(JsonPrimitive(it))}

        val call = JxglstuJSON.getDatum(cookie,lessonid)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                datumData.value = body
                if (body != null) {
                    if(body.contains("result"))
                        saveString("json", body)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getInfo(cookie : String) {

        val call = JxglstuHTML.getInfo(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("info", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })

        val call2 = JxglstuHTML.getMyProfile(cookie)

        call2.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("profile", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    val ProgramData = MutableLiveData<String?>()

    fun getProgram(cookie: String) {
        val call = JxglstuJSON.getProgram(cookie,studentId.value.toString())

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
        val call = JxglstuJSON.getProgramCompletion(cookie)

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
        val call = studentId.value?.let { JxglstuJSON.getProgramPerformance(cookie, it) }

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


    val teacherSearchData = MutableLiveData<String?>()

    fun getProgramPerformance(name: String = "",direction: String = "") {
        val call = teacher.searchTeacher(name=name, direction = direction)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                teacherSearchData.value = body
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val courseData = MutableLiveData<String?>()
    val courseRsponseData = MutableLiveData<String?>()
    fun searchCourse(cookie: String, className : String?,courseName : String?, semester : Int,courseId : String?) {
        val call = JxglstuJSON.searchCourse(cookie,studentId.value.toString(),semester,className,"1,100",courseName,courseId)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                courseData.value = response.code().toString()
                courseRsponseData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    val surveyListData = MutableLiveData<String?>()
    fun getSurveyList(cookie: String, semester : Int) {
        val call = JxglstuJSON.getSurveyList(cookie,studentId.value.toString(),semester)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                surveyListData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    val surveyData = MutableLiveData<String?>()
    fun getSurvey(cookie: String, id : String) {
        val call = JxglstuJSON.getSurveyInfo(cookie,id)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                surveyData.value = response?.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getSurveyToken(cookie: String, id : String) {
        val call = JxglstuJSON.getSurveyToken(cookie,id,"/for-std/lesson-survey/semester-index/${studentId.value}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        //         Log.d("cookies",response?.headers().toString())
                 saveString("SurveyCookie",response?.headers().toString().substringAfter("Set-Cookie:").substringBefore(";"))
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val surveyPostData = MutableLiveData<String?>()
    fun postSurvey(cookie : String,json: JsonObject){
        val call = JxglstuJSON.postSurvey(cookie,json)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                surveyPostData.value = response?.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    //val photo = MutableLiveData<ByteArray>()
    fun getPhoto(cookie : String){
        val call = JxglstuJSON.getPhoto(cookie,studentId.value.toString())

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

        val call = OneGoto.OneGoto(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun OneGotoCard(cookie : String)  {

        val call = OneGoto.OneGotoCard(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val BillsData = MutableLiveData<String>()
    fun CardGet(auth : String,page : Int) {// 创建一个Call对象，用于发送异步请求

        val size = prefs.getString("CardRequest","15")
        size?.let { Log.d("size", it) }
        val call = size?.let { ZJGDBill.Cardget(auth,page, it) }

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
        val call = ZJGDBill.getYue(auth)

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

        val feeitemid = when(type) {
            WEB -> "281"
            ELECTRIC -> "261"
            SHOWER -> "223"
        }
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
        val call = ZJGDBill.getFee(auth, typeId = feeitemid, room = rooms, level = levels, phoneNumber = phoneNumbers)

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
        val call = phoneNumber.let { loginCode.let { it1 -> guagua.getUserInfo(it, it1) } }
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                guaguaUserInfo.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val orderIdData = MutableLiveData<String?>()
    fun payStep1(auth: String,json: String,pay : Int,type: FeeType) {
        val feeitemid = when(type) {
            WEB -> 281
            ELECTRIC -> 261
            SHOWER -> 223
        }
        val call = ZJGDBill.pay(
            auth = auth,
            pay = pay,
            flag = "choose",
            paystep = 0,
            json = json,
            typeId = feeitemid,
            isWX = null,
            orderid = null,
            password = null,
            paytype = null,
            paytypeid = null,
            cardId = null
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                orderIdData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    val uuIdData = MutableLiveData<String?>()
    fun payStep2(auth: String,orderId : String) {

        val call = ZJGDBill.pay(
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
            paytypeid = 101,
            cardId = null
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                uuIdData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    val payResultData = MutableLiveData<String?>()
    fun payStep3(auth: String,orderId : String,password : String,uuid : String) {

        val call = ZJGDBill.pay(
            auth = auth,
            pay = null,
            flag = null,
            paystep = 2,
            json = null,
            typeId = 261,
            isWX = 0,
            orderid = orderId,
            password = password,
            paytype = "CARDTSM",
            paytypeid = 101,
            cardId = uuid
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                payResultData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    fun changeLimit(auth: String,json: JsonObject) {

        val call = ZJGDBill.changeLimit(auth,json)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("changeResult", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    var RangeData = MutableLiveData<String>()
    fun searchDate(auth : String, timeFrom : String, timeTo : String) {
        val call = ZJGDBill.searchDate(auth,timeFrom,timeTo)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                RangeData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val SearchBillsData = MutableLiveData<String>()
    fun searchBills(auth : String, info: String,page : Int) {
        val size = prefs.getString("CardRequest","15")
        size?.let { Log.d("size", it) }
        val call = size?.let { ZJGDBill.searchBills(auth,info,page, it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    SearchBillsData.value = response.body()?.string()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }


    var MonthData = MutableLiveData<String?>()
    fun getMonthBills(auth : String, dateStr: String) {
        val call = ZJGDBill.getMonthYue(auth,dateStr)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                MonthData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getToken()  {

        val codehttp = prefs.getString("code", "")
        var code = codehttp
        if (code != null) { code = code.substringAfter("=") }
        if (code != null) { code = code.substringBefore("]") }
        val http = codehttp?.substringAfter("[")?.substringBefore("]")


        val call = http?.let { code?.let { it1 -> One.getToken(it, it1) } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val json = response.body()?.string()
                    val data = Gson().fromJson(json, getTokenResponse::class.java)
                    if (data.msg == "success") {
                        token.value = data.data.access_token
                        saveString("bearer", data.data.access_token)
                    }
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

        val call = One.getBorrowBooks(token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                if (json.toString().contains("success")) {
                    val data = Gson().fromJson(json, BorrowBooksResponse::class.java)
                        val borrow = data.data.toString()
                    saveString("borrow",borrow)

                }
                 else saveString("borrow","未获取")

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getSubBooks(token : String)  {

        val call = One.getSubBooks(token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                if (json.toString().contains("success")) {
                    val data = Gson().fromJson(json, SubBooksResponse::class.java)
                        val sub = data.data.toString()
                    saveString("sub", sub)
                }
                else saveString("borrow","0")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }



    fun searchEmptyRoom(building_code : String,token : String)  {

        val call = One.searchEmptyRoom(building_code, "Bearer $token")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("emptyjson", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    val mailData = MutableLiveData<String?>()
    fun getMailURL(token : String)  {
        val secret = Encrypt.generateRandomHexString()
        val savedUsername = getPersonInfo().username + "@mail.hfut.edu.cn"
        val chipperText = Encrypt.encryptAesECB(savedUsername,secret)
        val cookie = "secret=$secret"
        val call = One.getMailURL(chipperText, "Bearer $token",cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                mailData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val PayData = MutableLiveData<String?>()
    fun getPay()  {

        val call = prefs.getString("Username","")?.let { One.getPay(it) }

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

        val call = Xuanqu.SearchXuanqu(code)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                XuanquData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun LePaoYunHome(Yuntoken : String) {

        val call = LePaoYun.getLePaoYunHome(Yuntoken)

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
        val call = CommuityTOKEN?.let { size?.let { it1 -> Community.getFailRate(it,name,page, it1) } }

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

        val call = CommuityTOKEN?.let { Community.getExam(it) }

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
        val call = JxglstuJSON.getExam(cookie,studentId.value.toString())

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
    fun getGrade(CommuityTOKEN: String,year : String,term : String) {

        val call = CommuityTOKEN?.let { Community.getGrade(it,year,term) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val responses = response.body()?.string()
                        saveString("Grade",responses )
                        GradeData.value = responses
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    GradeData.value = "错误"
                    t.printStackTrace() }
            })
        }
    }
    fun getAvgGrade(CommuityTOKEN: String) {

        val call = CommuityTOKEN?.let { Community.getAvgGrade(it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val responses = response.body()?.string()
                    saveString("Avg",responses )
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }
    fun getAllAvgGrade(CommuityTOKEN: String) {

        val call = CommuityTOKEN?.let { Community.getAllAvgGrade(it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val responses = response.body()?.string()
                    saveString("AvgAll",responses )
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    val libraryData = MutableLiveData<String>()
    fun SearchBooks(CommuityTOKEN: String,name: String) {

        val size = prefs.getString("BookRequest","15")
      //  size?.let { Log.d("size", it) }
        val call = CommuityTOKEN?.let { size?.let { it1 -> Community.searchBooks(it,name,"1", it1) } }
        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
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
    }

    fun GetCourse(CommuityTOKEN : String,studentId: String? = null) {

        val call = CommuityTOKEN?.let { Community.getCourse(it,studentId) }

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

        val call = CommuityTOKEN?.let { Community.switchShare(it, CommunityService.RequestJson(1)) }

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

        val call = Community.applyAdd(CommuityTOKEN,CommunityService.RequestJsonApply(username))

        NetWork.makeRequest(call,applyResponseMsg)
//        if (call != null) {
//            call.enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    applyResponseMsg.value = response.body()?.string()
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
//            })
//        }
    }
    val applyData = MutableLiveData<String>()
    fun getApplying(CommuityTOKEN : String) {
        val size = prefs.getString("CardRequest","15")
        val call = size?.let { Community.getApplyingList(CommuityTOKEN, it) }

        call?.let { NetWork.makeRequest(it,applyData) }
//        if (call != null) {
//            call.enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    applyData.value = response.body()?.string()
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
//            })
//        }
    }
    fun GetBorrowed(CommuityTOKEN: String,page : String) {
        val size = prefs.getString("BookRequest","15")
        val call = CommuityTOKEN?.let { size?.let { it1 -> Community.getBorrowedBook(it,page, it1) } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    saveString(LibraryItems.BORROWED.name, response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    fun GetHistory(CommuityTOKEN: String,page : String) {
        val size = prefs.getString("BookRequest","15")
        val call = CommuityTOKEN?.let { size?.let { it1 -> Community.getHistoyBook(it,page, it1) } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    saveString(LibraryItems.HISTORY.name, response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }
    fun getOverDueBook(CommuityTOKEN: String,page : String) {
        val size = prefs.getString("BookRequest","15")
        val call = CommuityTOKEN?.let { size?.let { it1 -> Community.getOverDueBook(it,page, it1) } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    saveString(LibraryItems.OVERDUE.name, response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    fun getToday(CommuityTOKEN : String) {

        val call = CommuityTOKEN?.let { Community.getToday(it) }

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

        val call = CommuityTOKEN?.let { Community.getFriends(it) }

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

        val call = CommuityTOKEN?.let { Community.checkApplying(it,CommunityService.RequestApplyingJson(id,if(isOk) 1 else 0)) }

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
        val call = (Semseter.getSemseterFromCloud()?.plus(20)).toString()
            ?.let { JxglstuJSON.getLessonIds(cookie,bizTypeId.toString(), it,studentid) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val json = response.body()?.string()
                    if (json != null) {
                        val id = Gson().fromJson(json, lessonResponse::class.java)
                        saveString("coursesNext",json)
                        lessonIdsNext.value = id.lessonIds
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

   // val datumDataNext = MutableLiveData<String?>()
    fun getDatumNext(cookie : String,lessonid: JsonObject) {

        val lessonIdsArray = JsonArray()
        lessonIds.value?.forEach {lessonIdsArray.add(JsonPrimitive(it))}

        val call = JxglstuJSON.getDatum(cookie,lessonid)

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
    fun getWeatherWarn() {
        val call = qWeather.getWeatherWarn()
        NetWork.makeRequest(call,weatherWarningData)
    }
    val weatherData = MutableLiveData<String?>()
    fun getWeather() {
        val call = qWeather.getWeather()
        NetWork.makeRequest(call,weatherData)
    }

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
                    Log.d(statusCode.toString(),"登陆失败")
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
                    Log.d("成功",response.headers()["Set-Cookie"].toString())
                    loginStuResponse.value = response.headers()["Set-Cookie"]
                } else {
                    Log.d("登失败",response.headers().toString())
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
}


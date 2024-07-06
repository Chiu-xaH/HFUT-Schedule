package com.hfut.schedule.ViewModel

//import com.hfut.schedule.logic.network.ServiceCreator.Login.OneGetNewTicketServiceCreator.client
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.logic.Enums.LibraryItems
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.Jxglstu.lessonResponse
import com.hfut.schedule.logic.datamodel.One.BorrowBooksResponse
import com.hfut.schedule.logic.datamodel.One.SubBooksResponse
import com.hfut.schedule.logic.datamodel.One.getTokenResponse
import com.hfut.schedule.logic.network.ServiceCreator.CommunitySreviceCreator
import com.hfut.schedule.logic.network.ServiceCreator.JwglappServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuHTMLServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuSurveyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.LePaoYunServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.NewsServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.One.OneServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.OneGotoServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.SearchEleServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.XuanquServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.ZJGDBillServiceCreator
import com.hfut.schedule.logic.network.api.CommunityService
import com.hfut.schedule.logic.network.api.FWDTService
import com.hfut.schedule.logic.network.api.JwglappService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LePaoYunService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.NewsService
import com.hfut.schedule.logic.network.api.OneService
import com.hfut.schedule.logic.network.api.XuanquService
import com.hfut.schedule.logic.network.api.ZJGDBillService
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.network.api.ServerService
import com.hfut.schedule.logic.network.ServiceCreator.ServerServiceCreator
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.getUserInfo
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginSuccessViewModel : ViewModel() {
    private val JxglstuJSON = JxglstuJSONServiceCreator.create(JxglstuService::class.java)
    private val JxglstuHTML = JxglstuHTMLServiceCreator.create(JxglstuService::class.java)
    private val OneGoto = OneGotoServiceCreator.create(LoginService::class.java)
    private val One = OneServiceCreator.create(OneService::class.java)
    private val ZJGDBill = ZJGDBillServiceCreator.create(ZJGDBillService::class.java)
    private val Xuanqu = XuanquServiceCreator.create(XuanquService::class.java)
    private val LePaoYun = LePaoYunServiceCreator.create(LePaoYunService::class.java)
    private val searchEle = SearchEleServiceCreator.create(FWDTService::class.java)
    private val CommunityLogin = LoginServiceCreator.create(CommunityService::class.java)
    private val JwglappLogin = JwglappServiceCreator.create(JwglappService::class.java)
    private val Community = CommunitySreviceCreator.create(CommunityService::class.java)
    private val Jwglapp = JwglappServiceCreator.create(JwglappService::class.java)
    private val News = NewsServiceCreator.create(NewsService::class.java)
    private val JxglstuSurvey = JxglstuSurveyServiceCreator.create(JxglstuService::class.java)
    private val server = ServerServiceCreator.create(ServerService::class.java)

    var studentId = MutableLiveData<Int>(99999)
    var lessonIds = MutableLiveData<List<Int>>()
    var token = MutableLiveData<String>()

    //val newFocus = MutableLiveData<String>()
    fun getData() {
        val call = server.getData()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Save("newFocus",response?.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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



    val NewsData = MutableLiveData<String?>()
    fun searchNews(title : String) {

        val call = News.searchNews(1,title)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                NewsData.value = response.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun GotoCommunity(cookie : String) {

        val call = CommunityLogin.LoginCommunity(cookie)

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

    fun getGrade(cookie: String) {
        val call = JxglstuJSON.getGrade(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save("grade", response.body()?.string())
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

    fun getStudentId(cookie : String) {

        val call = JxglstuJSON.getStudentId(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.headers()["Location"].toString().contains("/eams5-student/for-std/course-table/info/")) {
                    studentId.value = response.headers()["Location"].toString()
                        .substringAfter("/eams5-student/for-std/course-table/info/").toInt()
                } else studentId.value = 99999
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getLessonIds(cookie : String, bizTypeId : String,studentid : String) {
        //bizTypeId为年级数，例如23  //dataId为学生ID  //semesterId为学期Id，例如23-24第一学期为234

        val call = prefs.getString("semesterId","234")
            ?.let { JxglstuJSON.getLessonIds(cookie,bizTypeId, it,studentid) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val json = response.body()?.string()
                    if (json != null) {
                        val id = Gson().fromJson(json, lessonResponse::class.java)
                        SharePrefs.Save("courses",json)
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
                SharePrefs.Save("json", body)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getInfo(cookie : String) {

        val call = JxglstuHTML.getInfo(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save("info", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    fun getProgram(cookie: String) {
        val call = JxglstuJSON.getProgram(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save("program", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    val courseData = MutableLiveData<String?>()
    fun searchCourse(cookie: String, className : String?,courseName : String?, semester : Int) {
        val call = JxglstuJSON.searchCourse(cookie,studentId.value.toString(),semester,className,"1,20",courseName)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                courseData.value = response?.body()?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    val surveyListData = MutableLiveData<String?>()
    fun getSurveyList(cookie: String, semester : Int) {
        val call = JxglstuJSON.getSurveyList(cookie,studentId.value.toString(),semester)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                surveyListData.value = response?.body()?.string()
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
                 Save("SurveyCookie",response?.headers().toString().substringAfter("Set-Cookie:").substringBefore(";"))
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
                val bytes = response.body()?.bytes()
                // 将字节数组转换为Base64编码的字符串
                val base64String = Base64.encodeToString(bytes, Base64.DEFAULT)
                // 保存编码后的字符串
                Save("photo",base64String)
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
                SharePrefs.Save("cardyue",body )
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }


    fun changeLimit(auth: String,json: JsonObject) {

        val call = ZJGDBill.changeLimit(auth,json)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save("changeResult", response.body()?.string())
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
                        SharePrefs.Save("bearer", data.data.access_token)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }


    }

    val ElectricData = MutableLiveData<String>()
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
                    SharePrefs.Save("borrow",borrow)

                }
                 else SharePrefs.Save("borrow","未获取")

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
                    SharePrefs.Save("sub", sub)
                }
                else SharePrefs.Save("borrow","0")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }



    fun searchEmptyRoom(building_code : String,token : String)  {

        val call = One.searchEmptyRoom(building_code, "Bearer $token")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save("emptyjson", response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


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
                SharePrefs.Save("LePaoYun", response.body()?.string())
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
                    SharePrefs.Save("Exam", responses)
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

    fun getExamJXGLSTU(cookie: String) {
        val call = JxglstuJSON.getExam(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save("examJXGLSTU", response.body()?.string())
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
                        SharePrefs.Save("Grade",responses )
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
                    SharePrefs.Save("Avg",responses )
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
                    SharePrefs.Save("AvgAll",responses )
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
                    SharePrefs.Save("Library", response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    libraryData.value = "错误"
                    t.printStackTrace()
                }
            })
        }
    }

    fun GetCourse(CommuityTOKEN : String) {

        val call = CommuityTOKEN?.let { Community.getCourse(it) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    SharePrefs.Save("Course", response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }

    fun GetBorrowed(CommuityTOKEN: String,page : String) {
        val size = prefs.getString("BookRequest","15")
        val call = CommuityTOKEN?.let { size?.let { it1 -> Community.getBorrowedBook(it,page, it1) } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    SharePrefs.Save(LibraryItems.BORROWED.name, response.body()?.string())
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
                    SharePrefs.Save(LibraryItems.HISTORY.name, response.body()?.string())
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
                    SharePrefs.Save(LibraryItems.OVERDUE.name, response.body()?.string())
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
                    SharePrefs.Save("TodayNotice", response.body()?.string())
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
            })
        }
    }
    var lessonIdsNext = MutableLiveData<List<Int>>()
    fun getLessonIdsNext(cookie : String, bizTypeId : String,studentid : String) {
        //bizTypeId为年级数，例如23  //dataId为学生ID  //semesterId为学期Id，例如23-24第一学期为234

        val call = (prefs.getString("semesterId","234")?.toInt()?.plus(20)).toString()
            ?.let { JxglstuJSON.getLessonIds(cookie,bizTypeId, it,studentid) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val json = response.body()?.string()
                    if (json != null) {
                        val id = Gson().fromJson(json, lessonResponse::class.java)
                        SharePrefs.Save("coursesNext",json)
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
                SharePrefs.Save("jsonNext", body)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}
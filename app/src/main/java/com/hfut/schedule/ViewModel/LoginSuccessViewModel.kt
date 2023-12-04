package com.hfut.schedule.ViewModel

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.datamodel.One.BorrowBooksResponse
import com.hfut.schedule.logic.datamodel.One.CardResponse
import com.hfut.schedule.logic.datamodel.One.SubBooksResponse
import com.hfut.schedule.logic.datamodel.One.getTokenResponse

import com.hfut.schedule.logic.datamodel.Jxglstu.lessonIdsResponse
import com.hfut.schedule.logic.network.ServiceCreator.XuanquServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuHTMLServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.LibraryServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.OneGotoServiceCreator
//import com.hfut.schedule.logic.network.ServiceCreator.Login.OneGetNewTicketServiceCreator.client
import com.hfut.schedule.logic.network.ServiceCreator.Login.OneServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.ZJGDBillServiceCreator
import com.hfut.schedule.logic.network.api.XuanquService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.LibraryService
import com.hfut.schedule.logic.network.api.ZJGDBillService
import com.hfut.schedule.logic.network.api.OneService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class LoginSuccessViewModel : ViewModel() {
    private val JxglstuJSON = JxglstuJSONServiceCreator.create(JxglstuService::class.java)
    private val JxglstuHTML = JxglstuHTMLServiceCreator.create(JxglstuService::class.java)
    private val OneGoto = OneGotoServiceCreator.create(LoginService::class.java)
    private val One = OneServiceCreator.create(OneService::class.java)
    private val Library = LibraryServiceCreator.create(LibraryService::class.java)
    private val ZJGDBill = ZJGDBillServiceCreator.create(ZJGDBillService::class.java)
    private val Xuanqu = XuanquServiceCreator.create(XuanquService::class.java)

    var studentId = MutableLiveData<Int>()
    var lessonIds = MutableLiveData<List<Int>>()
    var token = MutableLiveData<String>()


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
                } else {
                    studentId.value = 99999
                }
                //Log.d("学生ID", studentId.value.toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getLessonIds(cookie : String, bizTypeId : String) {
        //bizTypeId为年级数，例如23  //dataId为学生ID  //semesterId为学期Id，例如23-24第一学期为234
        //这里先固定学期，每半年进行版本推送更新参数
        val call = JxglstuJSON.getLessonIds(cookie,bizTypeId,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                //Log.d("检验",json!!)
                if (json != null) {
                    val id = Gson().fromJson(json, lessonIdsResponse::class.java)
                    lessonIds.value = id.lessonIds

                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getDatum(cookie : String,lessonid: JsonObject) {

        val lessonIdsArray = JsonArray()
        lessonIds.value?.forEach {lessonIdsArray.add(JsonPrimitive(it))}

        val jsonObject = JsonObject().apply {
            add("lessonIds", lessonIdsArray)//课程ID
            addProperty("studentId", studentId.value)//学生ID
            addProperty("weekIndex", "")
        }
        //Log.d("xes",lessonid)

        val call = JxglstuJSON.getDatum(cookie,lessonid)


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()

                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("json","") != json){ sp.edit().putString("json", json).apply() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getInfo(cookie : String) {

        val call = JxglstuHTML.getInfo(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val info = response.body()?.string()

               // info?.let { Log.d("细腻些", it) }
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("info","") !=info ){ sp.edit().putString("info", info).apply() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getExam(cookie: String) {
        val call = JxglstuJSON.getExam(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val exam = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("exam","") !=exam ){ sp.edit().putString("exam", exam).apply() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getProgram(cookie: String) {
        val call = JxglstuJSON.getProgram(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val program = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("program","") !=program ){ sp.edit().putString("program", program).apply() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getGrade(cookie: String) {
        val call = JxglstuJSON.getGrade(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val grade = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("grade","") !=grade ){ sp.edit().putString("grade", grade).apply() }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }




    fun OneGoto(cookie : String)  {// 创建一个Call对象，用于发送异步请求

        val call = OneGoto.OneGoto(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                     // response.headers()["Location"]?.let { Log.d("响应", it.toString()) }
               // val finalUrl = (client.eventListener() as RedirectListener).getResponseUrl()
                // response.headers()["Location"]
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //   Log.d("VM","失败")
                //   code.value = "XXX"
                t.printStackTrace()
            }
        })


    }

    fun OneGotoCard(cookie : String)  {// 创建一个Call对象，用于发送异步请求

        val call = OneGoto.OneGotoCard(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }


    fun CardGet(auth : String,page : Int) {// 创建一个Call对象，用于发送异步请求

        val call = ZJGDBill.Cardget(auth,page)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val liushui = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("cardliushui","") !=liushui ){ sp.edit().putString("cardliushui", liushui).apply() }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }

    fun getyue(auth : String) {
        val call = ZJGDBill.getYue(auth)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val yue = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("cardyue","") !=yue ){ sp.edit().putString("cardyue", yue).apply() }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }

    fun searchDate(auth : String, timeFrom : String, timeTo : String) {
        val call = ZJGDBill.searchDate(auth,timeFrom,timeTo)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val yue = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("searchyue","") !=yue ){ sp.edit().putString("searchyue", yue).apply() }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun searchBills(auth : String, info: String,page : Int) {
        val call = ZJGDBill.searchBills(auth,info,page)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val yue = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("searchbills","") !=yue ){ sp.edit().putString("searchbills", yue).apply() }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    fun getMonthBills(auth : String, dateStr: String) {
        val call = ZJGDBill.getMonthYue(auth,dateStr)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val yue = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("monthbalance","") !=yue ){ sp.edit().putString("monthbalance", yue).apply() }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getToken()  {
        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val codehttp = prefs.getString("code", "")
        var code = codehttp
        if (code != null) { code = code.substringAfter("=") }
        if (code != null) { code = code.substringBefore("]") }
        val http = codehttp?.substringAfter("[")?.substringBefore("]")


        val call = http?.let { code?.let { it1 -> One.getToken(it, it1) } }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                         // response.body()?.let { Log.d("响应", it.string()) }
                    val json = response.body()?.string()
               //     json?.let { Log.d("json", it) }
                    val data = Gson().fromJson(json, getTokenResponse::class.java)
                    // response.headers()["Location"]
               //     data?.let { Log.d("JSON", it.toString()) }
                    if (data.msg == "success") {
                        token.value = data.data.access_token
                        val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        if(sp.getString("bearer","") != data.data.access_token){ sp.edit().putString("bearer", data.data.access_token).apply() }


                       // Log.d("token",token.value.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //   Log.d("VM","失败")
                    //   code.value = "XXX"
                    t.printStackTrace()
                }
            })
        }


    }


    fun getCard(token : String)  {

        val call = One.getCard(token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                if (json != null) {
                    if (json.contains("--")) {
                        val sp =
                            PreferenceManager.getDefaultSharedPreferences(MyApplication.context)

                        sp.edit().putString("card", "--").apply()

                    } else {
                        val data = Gson().fromJson(json, CardResponse::class.java)
                        val code = data.msg
                        if (code == "success") {
                            val card = data.data.toString()
                            val sp =
                                PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                            if (sp.getString("card", "") != card) {
                                sp.edit().putString("card", card).apply()
                            }
                        }
                    }
                } else {
                    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                    sp.edit().putString("card", "请登录刷新").apply()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
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
                        val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        if(sp.getString("borrow","") !=borrow ){ sp.edit().putString("borrow",borrow).apply() }

                }
                 else {
                    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                     sp.edit().putString("borrow","未获取到").apply()
                }
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
                        val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        if(sp.getString("sub","") !=sub ){ sp.edit().putString("sub", sub).apply() }

                }
                else {
                    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                    sp.edit().putString("borrow","未获取到").apply()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }



    fun searchEmptyRoom(building_code : String,token : String)  {

        val call = One.searchEmptyRoom(building_code,"Bearer " + token)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
               val emptyjson = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("emptyjson","") !=emptyjson ){ sp.edit().putString("emptyjson", emptyjson).apply() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }
    fun LibSearch(json : JsonObject)  {

        val call = Library.LibSearch(json)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val library = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("library","") !=library){ sp.edit().putString("library", library).apply() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })


    }

    fun SearchXuanqu(code : String) {

        val call = Xuanqu.SearchXuanqu(code)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val xuanqu = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("xuanqu","") !=xuanqu){ sp.edit().putString("xuanqu", xuanqu).apply() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })

    }

}
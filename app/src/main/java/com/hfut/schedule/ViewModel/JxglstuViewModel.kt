package com.hfut.schedule.ViewModel

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.MyApplication

import com.hfut.schedule.logic.datamodel.lessonIdsResponse
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuXMLServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.OneServiceCreator
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LoginService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class JxglstuViewModel : ViewModel() {
    private val api = JxglstuJSONServiceCreator.create(JxglstuService::class.java)
    private val api2 = JxglstuXMLServiceCreator.create(JxglstuService::class.java)
    private val api3 = OneServiceCreator.create(LoginService::class.java)
    var studentId = MutableLiveData<Int>()
    var lessonIds = MutableLiveData<List<Int>>()
  //  var body : String = ""

    fun Jxglstulogin(cookie : String) {

        val call = api.jxglstulogin(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    fun getStudentId(cookie : String) {

        val call = api.getStudentId(cookie)

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

     fun getLessonIds(cookie : String,bizTypeId : String) {
        //bizTypeId为年级数，例如23  //dataId为学生ID  //semesterId为学期Id，例如23-24第一学期为234
        //这里先固定学期，每半年进行版本推送更新参数
        val call = api.getLessonIds(cookie,bizTypeId,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()
                //Log.d("检验",json!!)
                if (json != null) {
                    val id = Gson().fromJson(json, lessonIdsResponse::class.java)
                    lessonIds.value = id.lessonIds
                }
            //    Log.d("测试",id.toString())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getDatum(cookie : String) {

        val lessonIdsArray = JsonArray()
        lessonIds.value?.forEach {lessonIdsArray.add(JsonPrimitive(it))}

        val jsonObject = JsonObject().apply {
            add("lessonIds", lessonIdsArray)//课程ID
            addProperty("studentId", studentId.value)//学生ID
            addProperty("weekIndex", "")
        }

        val call = api.getDatum(cookie,jsonObject)


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

        val call = api2.getInfo(cookie,studentId.value.toString())

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
        val call = api.getExam(cookie,studentId.value.toString())

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
        val call = api.getProgram(cookie,studentId.value.toString())

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
        val call = api.getGrade(cookie,studentId.value.toString())

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val grade = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("grade","") !=grade ){ sp.edit().putString("grade", grade).apply() }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    fun Onelogin(cookie : String)  {// 创建一个Call对象，用于发送异步请求

        val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val ticket = prefs.getString("ticket", "")

        val call = api3.Onelogin(ticket!!,cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.body()?.let { Log.d("响应", it.string()) }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //   Log.d("VM","失败")
                //   code.value = "XXX"
                t.printStackTrace()
            }
        })


    }

}
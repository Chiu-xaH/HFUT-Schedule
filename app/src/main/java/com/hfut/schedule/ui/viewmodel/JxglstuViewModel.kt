package com.hfut.schedule.ui.viewmodel

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
import com.hfut.schedule.logic.network.Servicecreator.JxglstuServiceCreator
import com.hfut.schedule.logic.network.api.JxglstuService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class JxglstuViewModel : ViewModel() {
    private val api = JxglstuServiceCreator.create(JxglstuService::class.java)
    var studentId = MutableLiveData<Int>()
    var lessonIds = MutableLiveData<List<Int>>()

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
                studentId.value = response.headers()["Location"].toString().substringAfter("/eams5-student/for-std/course-table/info/").toInt()

                Log.d("学生ID", studentId.value.toString())
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
                Log.d("检验",json!!)

                val id = Gson().fromJson(json,lessonIdsResponse::class.java)
                lessonIds.value =  id.lessonIds

            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
    fun getDatum(cookie : String) {

       // val lessonIds = listOf(420743,420869,420610,420579,424192,423169,420783,420812,420557,420811,423955,420444,423229,421016)//课程ID
        val lessonIdsArray = JsonArray()
        lessonIds.value?.forEach {lessonIdsArray.add(JsonPrimitive(it))}

        val jsonObject = JsonObject().apply {
            add("lessonIds", lessonIdsArray)
            addProperty("studentId", studentId.value)//学生ID
            addProperty("weekIndex", "")
        }

        val call = api.getDatum(cookie,jsonObject)


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = response.body()?.string()

                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if(sp.getString("datumjson","") != json){
                    sp.edit().putString("datumjson", json).apply()
                }

                Log.d("检验成果",json!!)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


}
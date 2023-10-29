package com.hfut.schedule.ui.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.hfut.schedule.logic.network.ServiceCreator.JxglstuServiceCreator
import com.hfut.schedule.logic.network.api.JxglstuService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class JxglstuViewModel : ViewModel() {
    val api = JxglstuServiceCreator.create(JxglstuService::class.java)
    var livedata = MutableLiveData<String>()

    fun getDatum(cookie : String) {

        val call = api.getDatum(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                livedata.value = body?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("测试","失")
                t.printStackTrace()
            }
        })
    }


    fun jxglstulogin(cookie : String) {

        val call = api.jxglstulogin(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                livedata.value = body?.string()

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("测试","失")
                t.printStackTrace()
            }
        })
    }

    fun getCourse(cookie : String) {

        val jsonObject = JsonObject().apply {
            addProperty("lessonIds", "[420743,420869,420610,420579,424192,423169,420783,420812,420557,420811,423955,420444,423229,421016]")
            addProperty("studentId", 170317)
            addProperty("weekIndex", "")
        }
        val call = api.getCourse(cookie,jsonObject)


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                livedata.value = body?.string()

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("测试","失")
                t.printStackTrace()
            }
        })
    }

    fun getDatum2(cookie : String) {

        val call = api.getDatum2(cookie)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()
                livedata.value = body?.string()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("测试","失")
                t.printStackTrace()
            }
        })
    }
}
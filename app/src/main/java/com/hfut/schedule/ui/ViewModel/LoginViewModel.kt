package com.hfut.schedule.ui.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.ServiceCreator.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.GetCookieServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.LoginServiceCreator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    var sessionLiveData = MutableLiveData<String>()
    var cookie2 = MutableLiveData<String>()
    var code = MutableLiveData<String>()
    var location = MutableLiveData<String>()


    private val api = LoginServiceCreator.create(LoginService::class.java)
    private val api2 = GetCookieServiceCreator.create(LoginService::class.java)
    private val api3 = GetAESKeyServiceCreator.create(LoginService::class.java)

    fun login(username : String,password : String,keys : String)  {// 创建一个Call对象，用于发送异步请求
        val cookies : String = sessionLiveData.value  + cookie2.value +";" + keys

        val call = api.login(cookies,username, password,"e1s1","submit")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()) {
                    code.value = response.code().toString()
                    location.value = response.headers()["Location"].toString()

                }
                 else {
                    location.value = response.headers()["Location"].toString()
                    code.value = response.code().toString()
                 }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                 Log.d("VM","失败")
                code.value = "XXX"
                t.printStackTrace()
            }
            })


    }

    fun getKey() {


        val call = api3.getKey()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()){
                    cookie2.value  = response.headers()["Set-Cookie"].toString()
                   // Log.d("测试","成功")

                }
                else Log.d("测试","失败，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("测试","失")
                t.printStackTrace()
            }
        })
    }

    fun getCookie() {

        val call = api2.getCookie()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()) {
                    sessionLiveData.value  = response.headers()["Set-Cookie"].toString().substringBefore(";").plus(";")
                    //Log.d("getCookie","成功")

                }
                else Log.d("测试q","失败，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("测试q","失")
                t.printStackTrace()
            }
        })
    }


    }




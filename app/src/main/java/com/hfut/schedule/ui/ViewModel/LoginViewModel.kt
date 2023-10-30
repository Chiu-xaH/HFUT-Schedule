package com.hfut.schedule.ui.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.network.ServiceCreator.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.GetCookieServiceCreator
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.ServiceCreator.LoginServiceCreator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    var livedata = MutableLiveData<String>()
    var sessionLiveData = MutableLiveData<String>()
    var cookie2 = MutableLiveData<String>()
    var code = MutableLiveData<String>()
    var location = MutableLiveData<String>()


    val api = LoginServiceCreator.create(LoginService::class.java)
    val api2 = GetCookieServiceCreator.create(LoginService::class.java)
    val api3 = GetAESKeyServiceCreator.create(LoginService::class.java)

    fun login(username : String,password : String,keys : String)  {// 创建一个Call对象，用于发送异步请求
        var Cookies : String? = sessionLiveData.value  + cookie2.value +";" + keys
        Log.d("验证",Cookies!!)
        val call = api.login(Cookies!!,username, password,"e1s1","submit")
        Log.d("账号",username)
        Log.d("密码",password)
        // 在子线程中执行请求，并在回调中处理响应结果
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // 请求成功，获取响应体
                val body = response.body()
                // 将响应体转换为字符串，并赋值给livedata
                livedata.value = body?.string()
                if(response.isSuccessful()) {
                    Log.d("响应码", response.code().toString())
                    Log.d("响应头", response.headers().toString())
                    Log.d("响应信息",response.message())
                    response.body()?.toString()?.let { Log.d("响应主体", it) }
                    code.value = response.code().toString()
                    location.value = response.headers()["Location"].toString()

                }
                 else {
                   //  ticket.value = response.headers()["Location"].toString().substringAfter("=")
                    location.value = response.headers()["Location"].toString()
                    Log.d("地址",location.value.toString())
                    Log.d("测试","失败")
                    Log.d("响应码", response.code().toString())
                   // Log.d("响应头", response.headers().toString())
                  //  Log.d("响应信息",response.message())
                   // Log.d("响应主体",response.body().toString())
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
                val body = response.body()
                livedata.value = body?.string()
                if(response.isSuccessful()){
                    cookie2.value  = response.headers()["Set-Cookie"].toString()
                    //Log.d("截获",cookie2.value.toString())
                    //Log.d("响应头", response.headers().toString())
                    Log.d("测试","成功")

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
                val body = response.body()
                livedata.value = body?.string()
                if(response.isSuccessful()) {

                    sessionLiveData.value  = response.headers()["Set-Cookie"].toString().substringBefore(";").plus(";")
                    Log.d("getCookie","成功")
                  //  Log.d("Cookie",SESSION)

                }
                else Log.d("测试q","失败，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("测试q","失")
                t.printStackTrace()
            }
        })
    }
    fun login2(username : String,password : String,keys : String) {// 创建一个Call对象，用于发送异步请求
        var Cookies : String? = sessionLiveData.value  + cookie2.value +";" + keys
        Log.d("验证",Cookies!!)
        val call = api.login2(Cookies!!,username, password,"e1s1","submit")
        Log.d("账号",username)
        Log.d("密码",password)
        // 在子线程中执行请求，并在回调中处理响应结果
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // 请求成功，获取响应体
                val body = response.body()
                // 将响应体转换为字符串，并赋值给livedata
                livedata.value = body?.string()
                if(response.isSuccessful()) {
                    Log.d("响应码", response.code().toString())
                    Log.d("响应头", response.headers().toString())
                    Log.d("响应信息",response.message())
                    Log.d("响应主体",response.body()?.toString()!!)

                }
                else {
                    location.value = response.headers()["Location"].toString().substringAfter("=")
                    Log.d("地址",location.value.toString())
                    Log.d("测试","失败")
                    Log.d("响应码", response.code().toString())
                    // Log.d("响应头", response.headers().toString())
                    //  Log.d("响应信息",response.message())
                    // Log.d("响应主体",response.body().toString())
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("VM","失败")
                t.printStackTrace()
            }
        })

    }

    }




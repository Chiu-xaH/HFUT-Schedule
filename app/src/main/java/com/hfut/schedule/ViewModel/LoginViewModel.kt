package com.hfut.schedule.ViewModel

import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.MyServiceCreator
import com.hfut.schedule.logic.network.api.MyService
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    var sessionLiveData = MutableLiveData<String>()
    var cookie2 = MutableLiveData<String>()
    var code = MutableLiveData<String>()
    var location = MutableLiveData<String>()
    var execution = MutableLiveData<String>()


    private val Login = LoginServiceCreator.create(LoginService::class.java)
    private val GetCookie = GetCookieServiceCreator.create(LoginService::class.java)
    private val GetAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val MyAPI = MyServiceCreator.create(MyService::class.java)


    fun login(username : String,password : String,keys : String)  {// 创建一个Call对象，用于发送异步请求

        val cookies : String = sessionLiveData.value  + cookie2.value +";" + keys
         val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (sp.getString("ONE", "") != cookies) { sp.edit().putString("ONE", cookies).apply() }

        val call = Login.login(cookies,username, password, execution.value!!,"submit")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {


                    location.value = response.headers()["Location"].toString()
                val TGC = response.headers()["Set-Cookie"].toString().substringBefore(";")
                    code.value = response.code().toString()
                    val ticket = response.headers()["Location"].toString().substringAfter("=")
                    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                    if (sp.getString("ticket", "") != ticket) { sp.edit().putString("ticket", ticket).apply() }
                if (sp.getString("TGC", "") != TGC) { sp.edit().putString("TGC", TGC).apply() }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
            })


    }

    fun getKey() {

        val call = GetAESKey.getKey()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()){ cookie2.value  = response.headers()["Set-Cookie"].toString() }
                else Log.d("测试","失败，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }

    fun getCookie() {

        val call = GetCookie.getCookie()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {


                val doc = Jsoup.parse( response.body()?.string())
                execution.value = doc.select("input[name=execution]").first()?.attr("value")

                if(response.isSuccessful()) { sessionLiveData.value  = response.headers()["Set-Cookie"].toString().substringBefore(";").plus(";") }
                else Log.d("失败","getKey，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }


    fun My() {
        val call = MyAPI.my()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if (sp.getString("my", "") !=body ) { sp.edit().putString("my", body).apply() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    }




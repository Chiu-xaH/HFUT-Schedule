package com.hfut.schedule.ViewModel

import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.SharePrefs
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
        SharePrefs.Save("ONE", cookies)

        val call = execution.value?.let { Login.login(cookies,username, password, it,"submit") }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                    location.value = response.headers()["Location"].toString()
                    val TGC = response.headers()["Set-Cookie"].toString().substringBefore(";")
                    code.value = response.code().toString()
                    val ticket = response.headers()["Location"].toString().substringAfter("=")
                    SharePrefs.Save("ticket", ticket)
                    SharePrefs.Save("TGC", TGC)

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    code.value = "XXX"
                    t.printStackTrace()
                }
            })
        }


    }

    fun getKey() {

        val call = GetAESKey.getKey()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()){ cookie2.value  = response.headers()["Set-Cookie"].toString() }
                //else Log.d("测试","失败，${response.code()},${response.message()}")
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
               // else Log.d("失败","getKey，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }


    fun My() {
        val call = MyAPI.my()

        PreferenceManager.getDefaultSharedPreferences(MyApplication.context).edit().putString("semesterId","234")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                SharePrefs.Save("my", response.body()?.string())
               // val semesterId = Gson().fromJson(response.body()?.string(), data4::class.java).semesterId
               // if(semesterId != null)
               // PreferenceManager.getDefaultSharedPreferences(MyApplication.context).edit().putString("semesterId",semesterId)
               // else  PreferenceManager.getDefaultSharedPreferences(MyApplication.context).edit().putString("semesterId","234")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }


    }




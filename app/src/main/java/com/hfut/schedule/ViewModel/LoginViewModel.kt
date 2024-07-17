package com.hfut.schedule.ViewModel

import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginWebVpnServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.MyServiceCreator
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.WebVpnService
import com.hfut.schedule.logic.utils.SharePrefs.Save
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


    private val LoginWebVpn = LoginWebVpnServiceCreator.create(WebVpnService::class.java)
    private val Login = LoginServiceCreator.create(LoginService::class.java)
    private val GetCookie = GetCookieServiceCreator.create(LoginService::class.java)
    private val GetAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val MyAPI = MyServiceCreator.create(MyService::class.java)


    var TICKET = MutableLiveData<String?>()
    fun login(username : String,password : String,keys : String,webVpn : Boolean)  {

        val cookies : String = sessionLiveData.value  + cookie2.value +";" + keys
        SharePrefs.Save("ONE", cookies)

      //  val ticketWebVpn = prefs.getString("WebVpn","")
        val ticket = webVpnTicket.value?.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
            ?.substringBefore(";")
       // Log.d("i",webVpnTicket.value.toString())
        val call =
            if(!webVpn) execution.value?.let { Login.login(cookies,username, password, it,"submit") }
            else execution.value?.let { LoginWebVpn.loginWebVpn("wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}",username, password, it,"submit") }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    location.value = response.headers()["Location"].toString()
                    val TGC = response.headers()["Set-Cookie"].toString().substringBefore(";")
                    code.value = response.code().toString()
                    val ticket = response.headers()["Location"].toString().substringAfter("=")
                    SharePrefs.Save("ticket", ticket)
                    SharePrefs.Save("TGC", TGC)
                    TICKET.value = ticket
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    code.value = "XXX $t"
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
    var webVpnTicket = MutableLiveData<String?>()

    fun getKeyWebVpn() {
    //    val ticketWebVpn = prefs.getString("WebVpn","")
    val ticket = webVpnTicket.value?.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
        ?.substringBefore(";")
        val call = LoginWebVpn.getKeyWebVpn("show_vpn=1; show_fast=0; heartbeat=1; show_faq=0; wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}; refresh=1")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()){
                    val responses = response.body()?.string()
                    Save("webVpnKey",responses?.substringAfter("LOGIN_FLAVORING=")?.substringBefore(";"))
                }
                //else Log.d("测试","失败，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }

    var status = MutableLiveData<Int?>()
    fun putKey(ticket : String) {
        //    val ticketWebVpn = prefs.getString("WebVpn","")

        val call = LoginWebVpn.putKey("wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                status.value = response?.code()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }
    fun getTicket() {

        val call = LoginWebVpn.getTicket()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()){
                    webVpnTicket.value = response.headers().toString()
                    val ticket =response.headers().toString().substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
                        ?.substringBefore(";")
                    Save("webVpnTicket",ticket)
                }
                //else Log.d("测试","失败，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }
    fun loginJxglstu() {
        val ticket = webVpnTicket.value?.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
            ?.substringBefore(";")
        val call = LoginWebVpn.loginJxglstu("wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
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




package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.util.network.NetWork
import com.hfut.schedule.logic.network.api.GithubService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.servicecreator.Login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebVpnServiceCreator
import com.hfut.schedule.logic.network.servicecreator.MyServiceCreator
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.WebVpnService
import com.hfut.schedule.logic.network.servicecreator.GithubServiceCreator
import com.hfut.schedule.logic.util.network.HfutCAS
import com.hfut.schedule.logic.util.network.NetWork.launchRequestSimple
import com.hfut.schedule.logic.util.network.SimpleStateHolder
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// 8个函数 这里是一切的地基：致敬传奇屎山 当时技术力不够，写的太耦合了，想最大程度保留原代码进行重构，根本无从下手...
class LoginViewModel : ViewModel() {
    var sessionLiveData = MutableLiveData<String>() //SESSIONID
    var jSessionId = MutableLiveData<String>() // JESSIONID
    var code = MutableLiveData<String?>()
    var location = MutableLiveData<String>()
    var execution = MutableLiveData<String>()

    private val LoginWebVpn = LoginWebVpnServiceCreator.create(WebVpnService::class.java)
    private val Login = LoginServiceCreator.create(LoginService::class.java)
    private val GetCookie = GetCookieServiceCreator.create(LoginService::class.java)
    private val GetAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val MyAPI = MyServiceCreator.create(MyService::class.java)


    var TICKET = MutableLiveData<String?>()
    fun login(username : String,password : String,keys : String,imageCode : String,webVpn : Boolean)  {

        val cookies : String = sessionLiveData.value  + jSessionId.value +";" + keys
        HfutCAS.casCookies = cookies

        val ticket = webVpnTicket.value?.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")?.substringBefore(";")
        val call =
            if(!webVpn) execution.value?.let { Login.login(cookie = cookies,username = username, password = password,execution = it,code = imageCode) }
            else execution.value?.let { LoginWebVpn.loginWebVpn(cookie ="wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}",username =username, password =password,execution= it, code = imageCode) }

        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                location.value = response.headers()["Location"].toString()
                val TGC = response.headers()["Set-Cookie"].toString().substringBefore(";")
                code.value = response.code().toString()
                val tickets = response.headers()["Location"].toString().substringAfter("=")
                saveString("ticket", tickets)
                saveString("TGC", TGC)
                TICKET.value = tickets
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
                if(response.isSuccessful){ jSessionId.value  = response.headers()["Set-Cookie"].toString() }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }

    var webVpnTicket = MutableLiveData<String?>()
    fun getKeyWebVpn() {
    val ticket = webVpnTicket.value?.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
        ?.substringBefore(";")
        val call = LoginWebVpn.getKeyWebVpn("show_vpn=1; show_fast=0; heartbeat=1; show_faq=0; wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}; refresh=1")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()){
                    val responses = response.body()?.string()
                    saveString("webVpnKey",responses?.substringAfter("LOGIN_FLAVORING=")?.substringBefore(";"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }

    var status = MutableLiveData<Int?>()
    fun putKey(ticket : String) {

        val call = LoginWebVpn.putKey("wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                status.value = response.code()
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

                if(response.isSuccessful){
                    webVpnTicket.value = response.headers().toString()
                    val ticket = response.headers().toString().substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
                        .substringBefore(";")
                    saveString("webVpnTicket",ticket)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }

    fun loginJxglstu() {
        val ticket = webVpnTicket.value?.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")?.substringBefore(";")
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
                val doc = response.body()?.string()?.let { Jsoup.parse(it) }
                if (doc != null) {
                    execution.value = doc.select("input[name=execution]").first()?.attr("value")
                }
                if(response.isSuccessful) { sessionLiveData.value  = response.headers()["Set-Cookie"].toString().substringBefore(";").plus(";") }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }

    fun getMyApi() {
        val call = MyAPI.my()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("my", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}




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
import com.hfut.schedule.logic.util.network.parse.JxglstuParseUtils
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// 9个函数
class LoginViewModel : ViewModel() {
    var sessionLiveData = MutableLiveData<String>() //SESSIONID
    var jsessionid = MutableLiveData<String>() // JESSIONID
    var code = MutableLiveData<String?>()
    var location = MutableLiveData<String>()
    var execution = MutableLiveData<String>()

    private val LoginWebVpn = LoginWebVpnServiceCreator.create(WebVpnService::class.java)
    private val Login = LoginServiceCreator.create(LoginService::class.java)
    private val GetCookie = GetCookieServiceCreator.create(LoginService::class.java)
    private val GetAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val MyAPI = MyServiceCreator.create(MyService::class.java)
    private val github = GithubServiceCreator.create(GithubService::class.java)

    var githubData = MutableLiveData<String?>()
    fun getStarsNum() = NetWork.makeRequest(github.getRepoInfo(),githubData)

    var TICKET = MutableLiveData<String?>()
    fun login(username : String,password : String,keys : String,imageCode : String,webVpn : Boolean)  {

        val cookies : String = sessionLiveData.value  + jsessionid.value +";" + keys
        JxglstuParseUtils.casCookies = cookies

      //  val ticketWebVpn = prefs.getString("WebVpn","")
        val ticket = webVpnTicket.value?.substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
            ?.substringBefore(";")
       // Log.d("i",webVpnTicket.value.toString())
        val call =
            if(!webVpn) execution.value?.let { Login.login(cookie = cookies,username = username, password = password,execution = it,code = imageCode) }
            else execution.value?.let { LoginWebVpn.loginWebVpn(cookie ="wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}",username =username, password =password,execution= it, code = imageCode) }

        if (call != null) {
            call.enqueue(object : Callback<ResponseBody> {
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
    }

    fun getKey() {

        val call = GetAESKey.getKey()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()){ jsessionid.value  = response.headers()["Set-Cookie"].toString() }
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
                    saveString("webVpnKey",responses?.substringAfter("LOGIN_FLAVORING=")?.substringBefore(";"))
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

                if(response.isSuccessful){
                    webVpnTicket.value = response.headers().toString()
                    val ticket = response.headers().toString().substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
                        .substringBefore(";")
                    saveString("webVpnTicket",ticket)
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
                val doc = response.body()?.string()?.let { Jsoup.parse(it) }
                if (doc != null) {
                    execution.value = doc.select("input[name=execution]").first()?.attr("value")
                }
                if(response.isSuccessful) { sessionLiveData.value  = response.headers()["Set-Cookie"].toString().substringBefore(";").plus(";") }
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
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                saveString("my", response.body()?.string())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}




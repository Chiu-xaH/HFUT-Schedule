package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.model.CasGetFlavorBean
import com.hfut.schedule.logic.model.CasGetFlavorResponse
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.servicecreator.Login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebVpnServiceCreator
import com.hfut.schedule.logic.network.servicecreator.MyServiceCreator
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.WebVpnService
import com.hfut.schedule.logic.network.repo.Repository.launchRequestSimple
import com.hfut.schedule.logic.util.network.state.CasInHFUT
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.component.onListenStateHolderForNetwork
import okhttp3.Headers
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

// 8个函数 这里是一切的地基：致敬传奇屎山 当时技术力不够，写的太耦合了，想最大程度保留原代码进行重构，根本无从下手...
class LoginViewModel : ViewModel() {
    val sessionLiveData = MutableLiveData<String>() //SESSIONID
    val jSessionId = StateHolder<CasGetFlavorBean>() // JESSIONID
    val code = MutableLiveData<String?>()
    val location = MutableLiveData<String>()
    val execution = MutableLiveData<String>()

    private val LoginWebVpn = LoginWebVpnServiceCreator.create(WebVpnService::class.java)
    private val Login = LoginServiceCreator.create(LoginService::class.java)
    private val GetCookie = GetCookieServiceCreator.create(LoginService::class.java)
    private val GetAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val MyAPI = MyServiceCreator.create(MyService::class.java)


    var TICKET = MutableLiveData<String?>()
    suspend fun login(username : String, password : String, keys : String, imageCode : String, webVpn : Boolean) =
        onListenStateHolderForNetwork(jSessionId) { jId ->
            if(webVpn) {
                onListenStateHolderForNetwork(webVpnTicket) { ticket ->
                    val call = execution.value?.let { LoginWebVpn.loginWebVpn(cookie ="wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}",username =username, password =password,execution= it, code = imageCode) }

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
            } else {
                val cookies : String = sessionLiveData.value  + jId.jSession +";" + keys
                CasInHFUT.casCookies = cookies

                val call = execution.value?.let { Login.login(cookie = cookies,username = username, password = password,execution = it,code = imageCode) }

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
        }

    suspend fun getKey() = launchRequestSimple(
        holder = jSessionId,
        request = { GetAESKey.getKey().awaitResponse() },
        transformSuccess = { headers,json -> parseKey(headers,json) }
    )
    private fun parseKey(headers: Headers,json : String) : CasGetFlavorBean {
        return headers["Set-Cookie"]?.let {
            CasGetFlavorBean(
                jSession = it,
                needCaptcha = try {
                    Gson().fromJson(json, CasGetFlavorResponse::class.java).needCaptcha
                } catch (e : Exception) {
                    throw Exception(e)
                }
            )
        } ?: throw Exception(headers.toString())
    }
//    {
//
//        val call = GetAESKey.getKey()
//
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if(response.isSuccessful){ jSessionId.value  = response.headers()["Set-Cookie"].toString() }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                code.value = "XXX"
//                t.printStackTrace()
//            }
//        })
//    }

    val webVpnTicket = StateHolder<String>()
    suspend fun getKeyWebVpn() = onListenStateHolderForNetwork(webVpnTicket) { ticket ->
        val call = LoginWebVpn.getKeyWebVpn("show_vpn=1; show_fast=0; heartbeat=1; show_faq=0; wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}; refresh=1")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful){
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

    val status = StateHolder<Boolean>()
    suspend fun putKey(ticket : String) = launchRequestSimple(
        holder = status ,
        request = { LoginWebVpn.putKey(MyApplication.WEBVPN_COOKIE_HEADER + ticket).awaitResponse() },
        transformSuccess = { _,_ -> true }
    )
//    {
//
//        val call =
//
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                status.value = response.code()
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                code.value = "XXX"
//                t.printStackTrace()
//            }
//        })
//    }

    suspend fun getTicket() = launchRequestSimple(
        holder = webVpnTicket,
        request = { LoginWebVpn.getTicket().awaitResponse() },
        transformSuccess = { headers,_ -> parseWebVpnCookie(headers) }
    )
    private suspend fun parseWebVpnCookie(headers: Headers) : String {
        try {
            val ticket = headers.toString().substringAfter(MyApplication.WEBVPN_COOKIE_HEADER)
                .substringBefore(";")
            DataStoreManager.saveWebVpnCookie(ticket)
            return ticket
//            saveString("webVpnTicket",ticket)
        } catch (e : Exception) { throw e }
    }
//    {
//
//        val call = LoginWebVpn.getTicket()
//
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//
//                if(response.isSuccessful){
//                    webVpnTicket.value = response.headers().toString()
//                    val ticket = response.headers().toString().substringAfter("wengine_vpn_ticketwebvpn_hfut_edu_cn=")
//                        .substringBefore(";")
//                    DataStoreManager.saveWebVpnCookie(ticket)
//                    saveString("webVpnTicket",ticket)
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                code.value = "XXX"
//                t.printStackTrace()
//            }
//        })
//    }

    suspend fun loginJxglstu() = onListenStateHolderForNetwork(webVpnTicket) { ticket ->
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




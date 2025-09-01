package com.hfut.schedule.viewmodel.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.enumeration.LoginType
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
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import okhttp3.Headers
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

// 8个函数 这里是一切的地基：致敬传奇屎山 当时技术力不够，写的太耦合了，想最大程度保留原代码进行重构，根本无从下手...
class LoginViewModel : ViewModel() {
    val jSessionId = StateHolder<CasGetFlavorBean>() // JESSIONID
    val code = MutableLiveData<String?>()
    val location = MutableLiveData<String>()
    //  execution,SESSIONID
    val execution = StateHolder<Pair<String, String>>()

    private val LoginWebVpn = LoginWebVpnServiceCreator.create(WebVpnService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)
    private val GetCookie = GetCookieServiceCreator.create(LoginService::class.java)
    private val GetAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val MyAPI = MyServiceCreator.create(MyService::class.java)



    var TICKET = MutableLiveData<String?>()
    suspend fun login(username : String, password : String, keys : String, imageCode : String, webVpn : Boolean) =
        onListenStateHolderForNetwork<CasGetFlavorBean,Unit>(jSessionId,null) { jId ->
            onListenStateHolderForNetwork<Pair<String, String>,Unit>(execution,null) {
                val execution = it.first
                val session = it.second
                val cookies : String = session + jId.jSession +";" + keys
                CasInHFUT.casCookies = cookies
                if(webVpn) {
                    onListenStateHolderForNetwork<String,Unit>(webVpnTicket,null) { ticket ->
                        val call = LoginWebVpn.loginWebVpn(
                            cookie ="wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}",
                            username =username,
                            password =password,
                            execution= execution,
                            code = imageCode
                        )

                        call.enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                location.value = response.headers()["Location"].toString()
                                code.value = response.code().toString()
                                val tickets = response.headers()["Location"].toString().substringAfter("=")
                                Log.d("CAS登录ticket",tickets)
                                saveString("ticket", tickets)
                                TICKET.value = tickets
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                code.value = "XXX"
                                t.printStackTrace()
                            }
                        })
                    }
                } else {
                    val call = login.loginCas(
                        cookie = cookies,
                        username = username,
                        password = password,
                        execution = execution,
                        code = imageCode,
                        url =
                            if(CasInHFUT.excludeJxglstu) LoginType.ONE.service
                            else LoginType.JXGLSTU.service
                    )
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            location.value = response.headers()["Location"].toString()
                            val tgc = response.headers()["Set-Cookie"].toString().substringBefore(";")
                            code.value = response.code().toString()
                            val tickets = response.headers()["Location"].toString().substringAfter("=")
                            Log.d("CAS登录ticket",tickets)
                            Log.d("CAS登录tgc",tgc)
                            saveString("ticket", tickets)
                            saveString("TGC", tgc)
                            TICKET.value = tickets
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            code.value = "XXX"
                            t.printStackTrace()
                        }
                    })
                }
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

    val webVpnTicket = StateHolder<String>()
    suspend fun getKeyWebVpn() = onListenStateHolderForNetwork<String, Unit>(webVpnTicket,null) { ticket ->
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


    suspend fun getTicket() = launchRequestSimple(
        holder = webVpnTicket,
        request = { LoginWebVpn.getTicket().awaitResponse() },
        transformSuccess = { headers,_ -> parseWebVpnCookie(headers) }
    )
    private suspend fun parseWebVpnCookie(headers: Headers) : String {
        try {
            val ticket = headers.toString().substringAfter(MyApplication.WEBVPN_COOKIE_HEADER)
                .substringBefore(";")
            // 保存cookie
            DataStoreManager.saveWebVpnCookie(ticket)
            return ticket
//            saveString("webVpnTicket",ticket)
        } catch (e : Exception) { throw e }
    }


    suspend fun loginJxglstu() = onListenStateHolderForNetwork<String, Unit>(webVpnTicket,null) { ticket ->
        Log.d("t",ticket)
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

    suspend fun getCookie() = launchRequestSimple(
        holder = execution,
        request = {
            GetCookie.getCookie(
                if(CasInHFUT.excludeJxglstu) LoginType.ONE.service
                else LoginType.JXGLSTU.service
            ).awaitResponse()
        },
        transformSuccess = { headers,html -> parseCookie(headers,html) }
    )
    private fun parseCookie(headers: Headers,html : String) : Pair<String, String> {
        try {
            val doc = Jsoup.parse(html)
            val execution = doc.select("input[name=execution]").first()?.attr("value") ?: "e1s1"
            val sessionLiveData  = headers["Set-Cookie"].toString().substringBefore(";").plus(";")
            return Pair(execution,sessionLiveData)
        } catch (e : Exception) { throw e }
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




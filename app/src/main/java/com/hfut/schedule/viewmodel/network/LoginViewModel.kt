package com.hfut.schedule.viewmodel.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CasLoginType
import com.hfut.schedule.logic.model.CasGetFlavorBean
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.WebVpnService
import com.hfut.schedule.logic.network.repo.hfut.CasLoginRepository
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.network.servicecreator.login.LoginServiceCreator
import com.hfut.schedule.logic.network.servicecreator.login.LoginWebVpnServiceCreator
import com.hfut.schedule.logic.network.util.CasInHFUT
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.xah.uicommon.util.LogUtil
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

// 8个函数 这里是一切的地基：致敬传奇屎山 当时技术力不够，写的太耦合了，想最大程度保留原代码进行重构，根本无从下手...
class LoginViewModel : ViewModel() {
    private val loginWebVpn = LoginWebVpnServiceCreator.create(WebVpnService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)

    val code = MutableLiveData<String?>()
    val location = MutableLiveData<String>()

    val jSessionId = StateHolder<CasGetFlavorBean>() // JSESSION
    suspend fun getKey() = CasLoginRepository.getEncryptKey(jSessionId)

    //  execution,SESSION
    val executionAndSession = StateHolder<Pair<String, String>>()
    suspend fun getCookie() = CasLoginRepository.getCasCookie(executionAndSession)


    var ticketStValue = MutableLiveData<String?>()
    suspend fun login(username : String, password : String, keys : String, imageCode : String) =
        onListenStateHolderForNetwork<CasGetFlavorBean,Unit>(jSessionId,null) { jId ->
            onListenStateHolderForNetwork<Pair<String, String>,Unit>(executionAndSession,null) {
                val execution = it.first
                val session = it.second
                val cookies : String = session + jId.jSession +";" + keys
                CasInHFUT.casCookies = cookies
                if(GlobalUIStateHolder.webVpn) {
                    onListenStateHolderForNetwork<String,Unit>(webVpnTicket,null) { ticket ->
                        val call = loginWebVpn.loginWebVpn(
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
                                LogUtil.debug("CAS登录ticket $tickets")
                                saveString("ticket", tickets)
                                ticketStValue.value = tickets
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
                            if(GlobalUIStateHolder.excludeJxglstu) CasLoginType.ONE.service
                            else CasLoginType.JXGLSTU.service
                    )
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            location.value = response.headers()["Location"].toString()
                            val tgc = response.headers()["Set-Cookie"].toString().substringBefore(";")
                            code.value = response.code().toString()
                            val tickets = response.headers()["Location"].toString().substringAfter("=")
                            LogUtil.debug("CAS登录ticket $tickets")
                            LogUtil.debug("CAS登录tgc $tgc")
                            saveString("ticket", tickets)
                            saveString("TGC", tgc)
                            ticketStValue.value = tickets
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            code.value = "XXX"
                            t.printStackTrace()
                        }
                    })
                }
            }
        }

    val webVpnTicket = StateHolder<String>()
    suspend fun getKeyWebVpn() = onListenStateHolderForNetwork<String, Unit>(webVpnTicket,null) { ticket ->
        val call = loginWebVpn.getKeyWebVpn("show_vpn=1; show_fast=0; heartbeat=1; show_faq=0; wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}; refresh=1")
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
    suspend fun putKey(ticket : String) = launchRequestState(
        holder = status ,
        request = { loginWebVpn.putKey(MyApplication.WEBVPN_COOKIE_HEADER + ticket) },
        transformSuccess = { _,_ -> true }
    )

    suspend fun getTicket() = launchRequestState(
        holder = webVpnTicket,
        request = { loginWebVpn.getTicket() },
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
        LogUtil.debug(ticket)
        val call = loginWebVpn.loginJxglstu("wengine_vpn_ticketwebvpn_hfut_edu_cn=${ticket}")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }

}




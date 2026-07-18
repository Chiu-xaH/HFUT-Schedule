package com.hfut.schedule.viewmodel.network

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.model.enumeration.CasLoginType
import com.hfut.schedule.network.api.model.response.json.cas.CasGetFlavorSessionDto
import com.hfut.schedule.network.api.inf.LoginService
import com.hfut.schedule.network.api.inf.WebVpnService
import com.hfut.schedule.logic.network.repo.CasLoginRepository
import com.hfut.schedule.logic.network.impl.LoginServiceCreator
import com.hfut.schedule.network.api.impl.LoginWebVpnServiceCreator
import com.hfut.schedule.logic.util.network.launchRequestState
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.network.onListenStateHolderForNetwork
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.xah.common.logic.util.LogUtil
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 8个函数 这里是一切的地基：致敬传奇屎山 当时技术力不够，写的太耦合了，想最大程度保留原代码进行重构，根本无从下手...
@Deprecated("为KMP适配计划的开始做铺垫，即将被合入至`NetworkViewModel`统一管理")
class LoginViewModel : ViewModel() {
    private val loginWebVpn = LoginWebVpnServiceCreator.create(WebVpnService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)

    @Deprecated("LiveData已不再作为本项目主力，请使用UiStateHolder")
    val code = MutableLiveData<String?>()
    @Deprecated("LiveData已不再作为本项目主力，请使用UiStateHolder")
    val location = MutableLiveData<String>()

    val jSessionId = UiStateHolder<CasGetFlavorSessionDto>() // JSESSION
    suspend fun getKey() = CasLoginRepository.getEncryptKey(jSessionId)

    //  execution,SESSION
    val executionAndSession = UiStateHolder<Pair<String, String>>()
    suspend fun getCookie() = CasLoginRepository.getCasCookie(executionAndSession)

    @Deprecated("LiveData已不再作为本项目主力，请使用UiStateHolder")
    var ticketStValue = MutableLiveData<String?>()
    suspend fun login(username : String, password : String, keys : String, imageCode : String) =
        onListenStateHolderForNetwork<CasGetFlavorSessionDto,Unit>(jSessionId,null) { jId ->
            onListenStateHolderForNetwork<Pair<String, String>,Unit>(executionAndSession,null) {
                val execution = it.first
                val session = it.second
                val cookies : String = session + jId.jSession +";" + keys
                GlobalUiStateHolder.casCookies = cookies
                if(GlobalUiStateHolder.webVpn) {
                    onListenStateHolderForNetwork<String,Unit>(webVpnTicket,null) { ticket ->
                        val call = loginWebVpn.loginWebVpn(
                            cookie ="${Constant.WEBVPN_COOKIE_HEADER}${ticket}",
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
                            if(GlobalUiStateHolder.excludeJxglstu) CasLoginType.ONE.service
                            else CasLoginType.JXGLSTU.service
                    )
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            location.value = response.headers()["Location"].toString()
                            val tgc = response.headers()["Set-Cookie"].toString().substringBefore(";")
                            code.value = response.code().toString()
                            val tickets = response.headers()["Location"].toString().substringAfter("=")
                            LogUtil.debug("CAS登录ticket=$tickets,tgc=$tgc")
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

    val webVpnTicket = UiStateHolder<String>()
    suspend fun getKeyWebVpn() = onListenStateHolderForNetwork<String, Unit>(webVpnTicket,null) { ticket ->
        val call = loginWebVpn.getKeyWebVpn("${Constant.WEBVPN_COOKIE_HEADER}${ticket}")
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

    val status = UiStateHolder<Boolean>()
    suspend fun putKey(ticket : String) = launchRequestState(
        holder = status ,
        request = { loginWebVpn.putKey(Constant.WEBVPN_COOKIE_HEADER + ticket) },
        transformSuccess = { _,_ -> true }
    )

    suspend fun getTicket() = launchRequestState(
        holder = webVpnTicket,
        request = { loginWebVpn.getTicket() },
        transformSuccess = { headers,_ -> parseWebVpnCookie(headers) }
    )
    private suspend fun parseWebVpnCookie(headers: Headers) : String {
        try {
            val ticket = headers.toString().substringAfter(Constant.WEBVPN_COOKIE_HEADER).substringBefore(";")
            // 保存cookie
            DataStoreManager.saveWebVpnCookie(ticket)
            return ticket
        } catch (e : Exception) { throw e }
    }

    suspend fun loginJxglstu() = onListenStateHolderForNetwork<String, Unit>(webVpnTicket,null) { ticket ->
        LogUtil.debug(ticket)
        val call = loginWebVpn.loginJxglstu("${Constant.WEBVPN_COOKIE_HEADER}${ticket}")

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




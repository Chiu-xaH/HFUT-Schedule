package com.hfut.schedule.logic.network.repo

import com.google.gson.Gson
import com.hfut.schedule.logic.enumeration.LoginType
import com.hfut.schedule.logic.model.CasGetFlavorBean
import com.hfut.schedule.logic.model.CasGetFlavorResponse
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.servicecreator.login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.login.GetCookieServiceCreator
import com.hfut.schedule.logic.util.network.state.CasInHFUT
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import okhttp3.Headers
import org.jsoup.Jsoup
import retrofit2.awaitResponse

object CasLoginRepository {
    private val getAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val getCookie = GetCookieServiceCreator.create(LoginService::class.java)

    suspend fun getCookie(execution : StateHolder<Pair<String, String>>) = launchRequestSimple(
        holder = execution,
        request = {
            getCookie.getCookie(
                if(GlobalUIStateHolder.excludeJxglstu) LoginType.ONE.service
                else LoginType.JXGLSTU.service
            ).awaitResponse()
        },
        transformSuccess = { headers,html -> parseCookie(headers,html) }
    )
    @JvmStatic
    private fun parseCookie(headers: Headers,html : String) : Pair<String, String> {
        try {
            val doc = Jsoup.parse(html)
            val execution = doc.select("input[name=execution]").first()?.attr("value") ?: "e1s1"
            val sessionLiveData  = headers["Set-Cookie"].toString().substringBefore(";").plus(";")
            return Pair(execution,sessionLiveData)
        } catch (e : Exception) { throw e }
    }

    suspend fun getKey(jSessionId : StateHolder<CasGetFlavorBean>) = launchRequestSimple(
        holder = jSessionId,
        request = { getAESKey.getKey().awaitResponse() },
        transformSuccess = { headers,json -> parseKey(headers,json) }
    )
    @JvmStatic
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
}
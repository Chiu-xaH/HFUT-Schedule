package com.hfut.schedule.logic.network.repo.hfut

import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CasLoginType
import com.hfut.schedule.logic.model.CasGetFlavorBean
import com.hfut.schedule.logic.model.CasGetFlavorResponse
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.util.launchRequestNone
import com.hfut.schedule.logic.network.util.launchRequestSimple
import com.hfut.schedule.logic.network.servicecreator.OneGotoServiceCreator
import com.hfut.schedule.logic.network.servicecreator.login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.servicecreator.login.LoginServiceCreator
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import okhttp3.Headers
import org.jsoup.Jsoup
import retrofit2.awaitResponse

object CasLoginRepository {
    private val getAESKey = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val getCookie = GetCookieServiceCreator.create(LoginService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)
    private val casOauth = OneGotoServiceCreator.create(LoginService::class.java)

    suspend fun gotoCommunity(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.COMMUNITY.service, cookie = cookie).awaitResponse()
    }
    suspend fun gotoZhiJian(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.ZHI_JIAN.service, cookie = cookie).awaitResponse()
    }
    suspend fun gotoLibrary(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.LIBRARY.service, cookie = cookie).awaitResponse()
    }
    suspend fun goToStu(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.STU.service, cookie = cookie).awaitResponse()
    }
    suspend fun goToPe(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.PE.service, cookie = cookie).awaitResponse()
    }
    suspend fun goToOne(cookie : String) = launchRequestNone {// 创建一个Call对象，用于发送异步请求
        casOauth.loginGoToOauth(
            "BsHfutEduPortal",
            MyApplication.Companion.ONE_URL + "home/index",
            cookie
        ).awaitResponse()
    }
    suspend fun goToHuiXin(cookie : String) = launchRequestNone {
        casOauth.loginGoToOauth(
            "Hfut2023Ydfwpt",
            MyApplication.Companion.HUI_XIN_URL + "berserker-auth/cas/oauth2url?oauth2url=${MyApplication.Companion.HUI_XIN_URL}berserker-base/redirect",
            cookie
        ).awaitResponse()
    }

    suspend fun getCasCookie(execution : StateHolder<Pair<String, String>>) = launchRequestSimple(
        holder = execution,
        request = {
            getCookie.getCookie(
                if (GlobalUIStateHolder.excludeJxglstu) CasLoginType.ONE.service
                else CasLoginType.JXGLSTU.service
            ).awaitResponse()
        },
        transformSuccess = { headers, html -> parseCookie(headers, html) }
    )
    @JvmStatic
    private fun parseCookie(headers: Headers, html : String) : Pair<String, String> {
        try {
            val doc = Jsoup.parse(html)
            val execution = doc.select("input[name=execution]").first()?.attr("value") ?: "e1s1"
            val sessionLiveData  = headers["Set-Cookie"].toString().substringBefore(";").plus(";")
            return Pair(execution,sessionLiveData)
        } catch (e : Exception) { throw e }
    }

    suspend fun getEncryptKey(jSessionId : StateHolder<CasGetFlavorBean>) = launchRequestSimple(
        holder = jSessionId,
        request = { getAESKey.getKey().awaitResponse() },
        transformSuccess = { headers, json -> parseKey(headers, json) }
    )
    @JvmStatic
    private fun parseKey(headers: Headers, json : String) : CasGetFlavorBean {
        return headers["Set-Cookie"]?.let {
            CasGetFlavorBean(
                jSession = it,
                needCaptcha = try {
                    Gson().fromJson(json, CasGetFlavorResponse::class.java).needCaptcha
                } catch (e: Exception) {
                    throw Exception(e)
                }
            )
        } ?: throw Exception(headers.toString())
    }
}
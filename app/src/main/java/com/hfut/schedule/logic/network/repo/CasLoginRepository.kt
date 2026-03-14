package com.hfut.schedule.logic.network.repo

import com.google.gson.Gson
import com.hfut.schedule.logic.enumeration.CasLoginType
import com.hfut.schedule.logic.model.CasGetFlavorBean
import com.hfut.schedule.logic.model.CasGetFlavorResponse
import com.hfut.schedule.logic.network.impl.AesKeyServiceCreator
import com.hfut.schedule.logic.network.impl.LoginServiceCreator
import com.hfut.schedule.logic.network.impl.OneGotoServiceCreator
import com.hfut.schedule.logic.util.network.launchRequestNone
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.network.api.LoginService
import com.hfut.schedule.network.impl.LoginGetCookieServiceCreator
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import okhttp3.Headers
import org.jsoup.Jsoup

object CasLoginRepository {
    private val getAESKey = AesKeyServiceCreator.create(LoginService::class.java)
    private val getCookie = LoginGetCookieServiceCreator.create(LoginService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)
    private val casOauth = OneGotoServiceCreator.create(LoginService::class.java)

    suspend fun gotoCommunity(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.COMMUNITY.service, cookie = cookie)
    }
    suspend fun gotoZhiJian(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.ZHI_JIAN.service, cookie = cookie)
    }
    suspend fun gotoLibrary(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.LIBRARY.service, cookie = cookie)
    }
    suspend fun goToStu(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.STU.service, cookie = cookie)
    }
    suspend fun goToPe(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.PE.service, cookie = cookie)
    }
    suspend fun goToOne(cookie : String) = launchRequestNone {// 创建一个Call对象，用于发送异步请求
        casOauth.loginGoToOauth(
            "BsHfutEduPortal",
            Constant.ONE_URL + "home/index",
            cookie
        )
    }
    suspend fun goToHuiXin(cookie : String) = launchRequestNone {
        casOauth.loginGoToOauth(
            "Hfut2023Ydfwpt",
            Constant.HUI_XIN_URL + "berserker-auth/cas/oauth2url?oauth2url=${Constant.HUI_XIN_URL}berserker-base/redirect",
            cookie
        )
    }

    suspend fun getCasCookie(execution : StateHolder<Pair<String, String>>) = launchRequestState(
        holder = execution,
        request = {
            getCookie.getCookie(
                if (GlobalUIStateHolder.excludeJxglstu) CasLoginType.ONE.service
                else CasLoginType.JXGLSTU.service
            )
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

    suspend fun getEncryptKey(jSessionId : StateHolder<CasGetFlavorBean>) = launchRequestState(
        holder = jSessionId,
        request = { getAESKey.getKey() },
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
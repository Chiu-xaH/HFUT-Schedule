package com.hfut.schedule.logic.network.repo


import com.hfut.schedule.logic.model.enumeration.CasLoginType
import com.hfut.schedule.network.api.model.response.json.cas.CasGetFlavorSessionDto
import com.hfut.schedule.network.api.model.response.json.cas.CasFlavorSessionResponse
import com.hfut.schedule.logic.network.impl.AesKeyServiceCreator
import com.hfut.schedule.logic.network.impl.LoginServiceCreator
import com.hfut.schedule.logic.network.impl.OneGotoServiceCreator
import com.hfut.schedule.logic.util.network.launchRequestNone
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.network.api.impl.LoginGetCookieServiceCreator
import com.hfut.schedule.network.api.inf.LoginService
import com.xah.common.logic.state.UiStateHolder
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.repo.CasLoginRepositoryInf
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import okhttp3.Headers
import org.jsoup.Jsoup

object CasLoginRepository : CasLoginRepositoryInf {
    private val getAESKey = AesKeyServiceCreator.create(LoginService::class.java)
    private val getCookie = LoginGetCookieServiceCreator.create(LoginService::class.java)
    private val login = LoginServiceCreator.create(LoginService::class.java)
    private val casOauth = OneGotoServiceCreator.create(LoginService::class.java)

    override suspend fun gotoCommunity(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.COMMUNITY.service, cookie = cookie)
    }
    override suspend fun gotoSecondClass(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.SECOND_CLASS.service, cookie = cookie)
    }
    override suspend fun gotoZhiJian(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.ZHI_JIAN.service, cookie = cookie)
    }
    override suspend fun gotoLibrary(cookie : String) = launchRequestNone {
        login.loginGoTo(service = CasLoginType.LIBRARY.service, cookie = cookie)
    }
    override suspend fun goToStu(cookie : String) = launchRequestNone {
        login.loginGoTo(service =  CasLoginType.STU.service, cookie = cookie)
    }
    override suspend fun goToPe(cookie : String) = launchRequestNone {
        login.loginGoTo(service =  CasLoginType.PE.service, cookie = cookie)
    }
    override suspend fun goToOne(cookie : String) = launchRequestNone {// 创建一个Call对象，用于发送异步请求
        casOauth.loginGoToOauth(
            "BsHfutEduPortal",
            Constant.ONE_URL + "home/index",
            cookie
        )
    }
    override suspend fun goToHuiXin(cookie : String) = launchRequestNone {
        casOauth.loginGoToOauth(
            "Hfut2023Ydfwpt",
            Constant.HUI_XIN_URL + "berserker-auth/cas/oauth2url?oauth2url=${Constant.HUI_XIN_URL}berserker-base/redirect",
            cookie
        )
    }

    override suspend fun getCasCookie(execution : UiStateHolder<Pair<String, String>>) = launchRequestState(
        holder = execution,
        request = {
            getCookie.getCookie(
                if (GlobalUiStateHolder.excludeJxglstu) CasLoginType.ONE.service
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

    override suspend fun getEncryptKey(jSessionId : UiStateHolder<CasGetFlavorSessionDto>) = launchRequestState(
        holder = jSessionId,
        request = { getAESKey.getKey() },
        transformSuccess = { headers, json -> parseKey(headers, json) }
    )
    @JvmStatic
    private fun parseKey(headers: Headers, json : String) : CasGetFlavorSessionDto {
        return headers["Set-Cookie"]?.let {
            CasGetFlavorSessionDto(
                jSession = it,
                needCaptcha = try {
                    GsonInstance.fromJson(json, CasFlavorSessionResponse::class.java).needCaptcha
                } catch (e: Exception) {
                    throw Exception(e)
                }
            )
        } ?: throw Exception(headers.toString())
    }
}
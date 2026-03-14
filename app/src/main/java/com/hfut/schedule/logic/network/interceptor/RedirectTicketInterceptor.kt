package com.hfut.schedule.logic.network.interceptor

import com.hfut.schedule.logic.util.network.encodeUrl
import com.hfut.schedule.logic.util.network.isNotBadRequest
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.LIBRARY_TOKEN
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.shared.LogUtil
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

class RedirectTicketInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.isRedirect) {
            val location = response.header("Location").toString()
            val ticket = location.substringAfter("ticket=")
            when {
                location.contains(Constant.STU_URL) -> {
                    // 学工系统的登录
                    // 向前重定向一次
                    val newRequest = request
                        .newBuilder()
                        .url(location)
                        .build()
                    val nextResponse = chain.proceed(newRequest)
                    parseLoginStu(nextResponse.headers,nextResponse.body?.string())
                    nextResponse.close()
                }
                location.contains(Constant.COMMUNITY_URL) -> {
                    // 智慧社区的登录
                    GoToInterceptorState.toCommunityTicket.value = ticket
                }
                location.contains(Constant.ZHI_JIAN_URL) -> {
                    // 指间工大登录
                    // 向前重定向一次
                    val newRequest = request
                        .newBuilder()
                        .url(location)
                        .build()
                    val nextResponse = chain.proceed(newRequest)
                    val cookie = parseLoginZhiJian(nextResponse.headers)
                    val homeLocation = nextResponse.header("Location")
                    nextResponse.close()
                    cookie?.let {
                        // 向主页发送一个请求 使cookie生效
                        val checkRequest = request
                            .newBuilder()
                            .header("Cookie", cookie)
                            .url(
                                homeLocation ?: (
                                        Constant.ZHI_JIAN_URL +
                                                "wui/cas-entrance.jsp;jsessionid=${
                                                    it.substringAfter("=")
                                                }?path=${
                                                    encodeUrl(encodeUrl(Constant.ZHI_JIAN_URL + "wui/index.html#/main"))
                                                }&ssoType=CAS"
                                        )
                            )
                            .build()
                        val checkResponse = chain.proceed(checkRequest)
                        LogUtil.debug("CAS 指尖工大登录 ${checkResponse.code}")
                        if(isNotBadRequest(checkResponse.code)) {
                            showToast("指间工大登陆成功")
                        } else {
                            showToast("指间工大登陆失败 ${checkResponse.code}")
                        }
                        checkResponse.close()
                    }
                }
                location.contains(Constant.JXGLSTU_URL) -> {
                    // 教务系统的登录
                    // 向前重定向一次
                    val newRequest = request
                        .newBuilder()
                        .url(location)
                        .build()
                    val nextResponse = chain.proceed(newRequest)
                    parseLoginJxglstu(nextResponse.headers)
                    nextResponse.close()
                }
                location.contains(Constant.NEW_LIBRARY_URL) -> {
                    // 图书馆登录
                    // 向前重定向一次
                    val newRequest = request
                        .newBuilder()
                        .url(location)
                        .build()
                    val nextResponse = chain.proceed(newRequest)
                    parseLoginLibrary(nextResponse.headers)
                    nextResponse.close()
                }
                location.contains(Constant.PE_URL) -> {
                    // 体测平台
                    val token = "PHPSESSID=$ticket"
                    saveString("PE", token)
                    // 向前重定向一次
                    // 直到响应不是302
                    val newRequest = request
                        .newBuilder()
                        .url(location)
                        .build()
                    val nextResponse = chain.proceed(newRequest)
                    val location2 = nextResponse.headers["Location"]
                    nextResponse.close()
                    if(location2 != null) {
                        val newRequest2 = request
                            .newBuilder()
                            .url(location2)
                            .header("Cookie",token)
                            .build()
                        val nextResponse2 = chain.proceed(newRequest2)
                        nextResponse2.close()
                        showToast("体测平台登陆成功")
                    } else {
                        showToast("体测平台登录失败")
                    }
                }
            }
        }
        return response
    }
}

private fun parseLoginStu(headers: Headers,json: String?) =  try {
    if(json == null) throw Exception("无内容")

    val sId = getPersonInfo().studentId ?: throw Exception("无学号")
    if(!json.contains(sId)) throw Exception("登陆失败")
    val cookie = headers["Set-Cookie"] ?: throw Exception("解析失败")

    val prefix = "_WEU="
    val weu = prefix + cookie.substringAfter(prefix).substringBefore(";")
    saveString("stu",weu)
    LogUtil.debug("学工系统登录 $weu")
    showToast("学工系统登陆成功")
} catch (e : Exception) {
    LogUtil.error(e)
}


private fun parseLoginJxglstu(headers: Headers) = try {
    val cookie = headers.toString().substringAfter("SESSION=").substringBefore(";")
    saveString("redirect", "SESSION=$cookie")
    showToast("教务系统登陆成功")
} catch (e : Exception) {
    LogUtil.error(e)
}

private fun parseLoginZhiJian(headers: Headers) : String? = try {
    val token = headers["Location"]?.substringAfter("jsessionid=")?.substringBefore("?")
    if (token != null) {
        val r = "ecology_JSessionid=$token"
        saveString("ZhiJian", r)
        r
    } else {
        showToast("指间工大登陆失败")
        null
    }
} catch (e : Exception) {
    LogUtil.error(e)
    null
}

private fun parseLoginLibrary(headers: Headers)  = try {
    val token = headers.toString().substringAfter("Authorization=").substringBefore(";")
    if (token.contains("ey")) {
        saveString(LIBRARY_TOKEN, "Bearer $token")
        showToast("图书馆登陆成功")
    } else {
        showToast("图书馆登陆失败")
    }
} catch (e : Exception) {
    LogUtil.error(e)
}


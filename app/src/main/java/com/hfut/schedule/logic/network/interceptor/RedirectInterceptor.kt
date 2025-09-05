package com.hfut.schedule.logic.network.interceptor

import android.util.Log
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.text.substringAfter

class RedirectInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.isRedirect) {
            val location = response.header("Location").toString()
            val ticket = location.substringAfter("ticket=")
            when {
                location.contains(MyApplication.STU_URL) -> {
                    // 学工系统的登录
                    // 向前重定向一次
                    val newRequest = request
                        .newBuilder()
                        .url(location)
                        .build()
                    val nextResponse = chain.proceed(newRequest)
                    parseLoginStu(nextResponse.headers,nextResponse.body?.string())
                }
                location.contains(MyApplication.COMMUNITY_URL) -> {
                    // 智慧社区的登录
                    CasGoToInterceptorState.toCommunityTicket.value = ticket
                }
                location.contains(MyApplication.JXGLSTU_URL) -> {
                    // 教务系统的登录
                    // 向前重定向一次
                    val newRequest = request
                        .newBuilder()
                        .url(location)
                        .build()
                    val nextResponse = chain.proceed(newRequest)
                    parseLoginJxglstu(nextResponse.headers)
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
    Log.d("学工系统登录",weu)
    showToast("学工平台登陆成功")
} catch (e : Exception) {
    e.printStackTrace()
}


fun parseLoginJxglstu(headers: Headers) = try {
    val cookie = headers.toString().substringAfter("SESSION=").substringBefore(";")
    saveString("redirect", "SESSION=$cookie")
    showToast("教务系统登陆成功")
} catch (e : Exception) {
    e.printStackTrace()
}
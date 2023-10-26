package com.hfut.schedule.logic.network.interceptor

import android.se.omapi.Session
import android.text.TextUtils
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
//本拦截器是解析SESSION的，解析后将其通过拦截器添加到POST请求头
class ReceiveInterceptor : Interceptor {
    var SESSION : String = ""
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        SESSION = response.headers("Set-Cookie").toString()
            .substringAfter("[")
            .substringBefore(";")

        Log.d("需要提交的",SESSION)

        return response
    }

}
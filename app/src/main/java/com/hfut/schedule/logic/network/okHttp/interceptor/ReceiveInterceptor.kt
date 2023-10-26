package com.hfut.schedule.logic.network.okHttp.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
//本拦截器是解析SESSION的，解析后将其通过拦截器添加到POST请求头
class ReceiveInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        for(header in response.headers("Set-Cookie")) {
            Log.d("Set",header)
        }
        return response
    }
}
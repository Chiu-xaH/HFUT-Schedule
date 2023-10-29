package com.hfut.schedule.logic.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(chain.request())
        val headers = response.headers.toString()
        Log.d("拦截器",headers)

        return response
    }
}
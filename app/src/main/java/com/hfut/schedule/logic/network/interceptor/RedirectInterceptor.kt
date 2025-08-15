package com.hfut.schedule.logic.network.interceptor

import com.hfut.schedule.logic.util.storage.SharedPrefs
import okhttp3.Interceptor
import okhttp3.Response

class RedirectInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.isRedirect) {
            val location = response.header("Location").toString()
//                Log.d("测试1",location)
            val ticket = location.substringAfter("=")

            SharedPrefs.saveString("TICKET",ticket)
            val newRequest = request.newBuilder()
                .url(location)
                .build()
            val newResponse = chain.proceed(newRequest)
            val str = newResponse.headers
//                Log.d("测试0",str.toString())

            val cookie = str.toString().substringAfter("SESSION=").substringBefore(";")
//                Log.d("测试1",cookie.toString())
            SharedPrefs.saveString("redirect", "SESSION=$cookie")
        }
        return response
    }
}

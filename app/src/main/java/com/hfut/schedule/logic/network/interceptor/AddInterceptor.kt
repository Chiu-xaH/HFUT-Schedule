package com.hfut.schedule.logic.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
//提交到POST，从RI解析出来的Cookie，只提交SESSION即可
class AddInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        //var cookie = ReceiveInterceptor().SESSION

        request.newBuilder()
            .addHeader("Cookie","zzzzzzzzz")
            .build()

        Log.d("传送的SE", request.headers.toString())



        return response
    }

}
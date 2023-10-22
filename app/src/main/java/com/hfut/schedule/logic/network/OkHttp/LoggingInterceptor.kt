package com.hfut.cookiedemo

import android.net.Network
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.String


 class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
       val request = chain.request()
        //request.newBuilder()

     //   val request2 = chain.proceed(chain.request())
        Log.d("请求头",request.headers.toString())
        val response = chain.proceed(request)
        Log.d("响应头",response.headers.toString())
        return response
    }
}

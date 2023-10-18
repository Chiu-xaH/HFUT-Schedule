package com.hfut.schedule.logic.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {


    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            val cookies = response.headers("Set-Cookie")
            // Do something with cookies
            response
        }
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestWithCookies = originalRequest.newBuilder()
                .header("Cookie", cookies.joinToString(";"))
                .build()
            chain.proceed(requestWithCookies)
        }
        .build()


    const val URL = "http://jxglstu.hfut.edu.cn/"

    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val response = retrofit.create(LoginService::class.java).getCookies()
    val cookies = response.headers()
}
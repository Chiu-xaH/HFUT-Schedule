package com.hfut.schedule.logic.network

import com.chiuxah.weather.MyApplication
import com.hfut.schedule.logic.network.OkHttp.AddCookiesInterceptor
import com.hfut.schedule.logic.network.OkHttp.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceCreator {
    const val URL = "http://jxglstu.hfut.edu.cn/"

    private val client by lazy {
        OkHttpClient.Builder().apply {
            connectTimeout(5L, TimeUnit.SECONDS)
            addInterceptor(AddCookiesInterceptor(MyApplication.context))
            addInterceptor(SaveCookiesInterceptor(MyApplication.context))
        }.build()
    }

    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}
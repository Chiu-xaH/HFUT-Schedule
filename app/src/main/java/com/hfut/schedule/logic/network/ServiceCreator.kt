package com.hfut.schedule.logic.network

import android.content.Context
import com.chiuxah.weather.MyApplication
import com.hfut.schedule.logic.network.OkHttp.AddCookiesInterceptor
import com.hfut.schedule.logic.network.OkHttp.SaveCookiesInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceCreator {
    const val URL = "https://cas.hfut.edu.cn/"

    private val client by lazy {
        OkHttpClient.Builder().apply {
            connectTimeout(5L, TimeUnit.SECONDS)
            addInterceptor(AddCookiesInterceptor(MyApplication.context))
            addInterceptor(SaveCookiesInterceptor(MyApplication.context))
        }.build()
    }
    //lateinit property context has not been initialized问题

    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>):T = retrofit.create(service)

}
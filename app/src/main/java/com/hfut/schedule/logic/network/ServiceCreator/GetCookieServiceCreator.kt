package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.logic.network.okHttp.interceptor.ReceiveInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetCookieServiceCreator {
    const val URL = "https://cas.hfut.edu.cn/"

    val Client = OkHttpClient.Builder()
        .addNetworkInterceptor(ReceiveInterceptor())//获取响应,解析出Cookie
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
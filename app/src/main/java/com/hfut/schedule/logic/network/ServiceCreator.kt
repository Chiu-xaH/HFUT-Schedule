package com.hfut.schedule.logic.network


import com.hfut.schedule.logic.network.OkHttp.PersistenceCookieJar
import com.hfut.schedule.logic.network.OkHttp.ReceiveCookieInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object ServiceCreator {
    const val URL = "https://cas.hfut.edu.cn/"

    val Client = OkHttpClient.Builder()
        .addNetworkInterceptor(ReceiveCookieInterceptor())
        .cookieJar(PersistenceCookieJar())
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)

}
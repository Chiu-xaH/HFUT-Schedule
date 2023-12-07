package com.hfut.schedule.logic.network.ServiceCreator.OneGoto

import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.network.interceptor.GotoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OneGotoServiceCreator {
    val Client = OkHttpClient.Builder()
        .addNetworkInterceptor(GotoInterceptor())
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.LoginURL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
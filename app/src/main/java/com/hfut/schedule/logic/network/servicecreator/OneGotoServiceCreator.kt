package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.network.interceptor.GotoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OneGotoServiceCreator {
    val Client = OkHttpClient.Builder()
        .addNetworkInterceptor(GotoInterceptor())
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.CAS_LOGIN_URL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
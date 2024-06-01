package com.hfut.schedule.logic.network.ServiceCreator.Login

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.network.interceptor.RedirectInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object LoginWebServiceCreator {


    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.loginWebURL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
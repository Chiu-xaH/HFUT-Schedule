package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetCookieServiceCreator {

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.LoginURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
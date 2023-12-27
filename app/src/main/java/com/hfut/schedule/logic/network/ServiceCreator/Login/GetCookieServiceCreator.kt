package com.hfut.schedule.logic.network.ServiceCreator.Login

import com.hfut.schedule.App.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object GetCookieServiceCreator {

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.LoginURL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
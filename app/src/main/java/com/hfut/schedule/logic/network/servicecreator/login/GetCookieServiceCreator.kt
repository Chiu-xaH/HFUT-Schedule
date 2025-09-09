package com.hfut.schedule.logic.network.servicecreator.login

import com.hfut.schedule.application.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object GetCookieServiceCreator {

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.CAS_LOGIN_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
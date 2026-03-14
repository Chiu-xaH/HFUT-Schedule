package com.hfut.schedule.network.impl

import com.hfut.schedule.network.util.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object LoginGetCookieServiceCreator {

    val retrofit = Retrofit.Builder()
        .baseUrl(Constant.CAS_LOGIN_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
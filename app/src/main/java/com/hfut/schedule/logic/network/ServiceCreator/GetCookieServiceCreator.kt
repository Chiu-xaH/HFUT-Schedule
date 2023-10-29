package com.hfut.schedule.logic.network.ServiceCreator

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetCookieServiceCreator {
    const val URL = "https://cas.hfut.edu.cn/"


    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
package com.hfut.schedule.logic.network.ServiceCreator

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JxglstuServiceCreator {


    var baseURL : String = "http://jxglstu.hfut.edu.cn/"

    val Client = OkHttpClient.Builder()
        .followRedirects(false)
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)

}
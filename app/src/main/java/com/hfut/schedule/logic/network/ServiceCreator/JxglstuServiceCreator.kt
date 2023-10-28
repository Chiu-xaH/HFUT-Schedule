package com.hfut.schedule.logic.network.ServiceCreator

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JxglstuServiceCreator {
    const val URL = "http://jxglstu.hfut.edu.cn/"

   // val Client = OkHttpClient.Builder()
      //  .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
       // .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)

}
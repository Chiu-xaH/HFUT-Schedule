package com.hfut.schedule.logic.network.ServiceCreator.Jxglstu

import com.hfut.schedule.MyApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object JxglstuXMLServiceCreator {

    val Client = OkHttpClient.Builder()
    //    .followRedirects(false)
        .build()

    val retrofit = Retrofit.Builder()
       .baseUrl(MyApplication.JxglstuURL)
        .client(Client)
         .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
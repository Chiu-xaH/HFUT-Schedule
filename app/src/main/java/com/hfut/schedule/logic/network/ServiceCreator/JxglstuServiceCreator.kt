package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.MyApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JxglstuServiceCreator {

    val Client = OkHttpClient.Builder()
        .followRedirects(false)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.JxglstuURL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)

}
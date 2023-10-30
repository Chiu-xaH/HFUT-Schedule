package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.logic.datamodel.URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JxglstuServiceCreator {

    val retrofit = Retrofit.Builder()
        .baseUrl(URL().JxglstuURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)

}
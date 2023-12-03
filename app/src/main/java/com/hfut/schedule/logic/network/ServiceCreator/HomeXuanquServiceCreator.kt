package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object HomeXuanquServiceCreator {
    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.HomeXuanquURL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
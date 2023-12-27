package com.hfut.schedule.logic.network.ServiceCreator.One

import com.hfut.schedule.App.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LibraryServiceCreator {

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.LibURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
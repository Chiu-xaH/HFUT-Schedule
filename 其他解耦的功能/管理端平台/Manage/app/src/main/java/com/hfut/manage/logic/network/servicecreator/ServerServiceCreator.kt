package com.hfut.manage.logic.network.servicecreator

import com.hfut.manage.MyApplication
import com.hfut.manage.ui.getURL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerServiceCreator {

    val retrofit = Retrofit.Builder()
        .baseUrl(getURL())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
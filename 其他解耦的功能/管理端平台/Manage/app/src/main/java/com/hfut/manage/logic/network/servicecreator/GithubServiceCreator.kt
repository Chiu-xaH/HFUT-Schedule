package com.hfut.manage.logic.network.servicecreator

import com.hfut.manage.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GithubServiceCreator {

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.GithubURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
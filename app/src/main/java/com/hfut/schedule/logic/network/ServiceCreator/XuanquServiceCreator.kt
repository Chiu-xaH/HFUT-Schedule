package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object XuanquServiceCreator {
    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.XuanquURL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
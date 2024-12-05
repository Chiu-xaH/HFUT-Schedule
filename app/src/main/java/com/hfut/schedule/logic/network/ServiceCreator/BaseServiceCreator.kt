package com.hfut.schedule.logic.network.ServiceCreator

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

open class BaseServiceCreator(url: String,isJSONorXML : Boolean = true) {
    private var client = OkHttpClient.Builder()
        .build()

    open fun setClient(newClient : OkHttpClient) {
        this.client = newClient
    }


    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(client)
        .addConverterFactory(if (isJSONorXML) GsonConverterFactory.create() else ScalarsConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified T> create(): T = create(T::class.java)
}
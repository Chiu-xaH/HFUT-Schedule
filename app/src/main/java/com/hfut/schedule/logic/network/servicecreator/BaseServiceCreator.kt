package com.hfut.schedule.logic.network.servicecreator

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

open class BaseServiceCreator(
    url: String,
//    isJSONorXML: Boolean = true,
    client: OkHttpClient = OkHttpClient.Builder().build()
) {
    protected val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(client)
        .addConverterFactory(
//            if (isJSONorXML)
                    GsonConverterFactory.create()
//            else ScalarsConverterFactory.create()
        )
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified T> create(): T = create(T::class.java)
}

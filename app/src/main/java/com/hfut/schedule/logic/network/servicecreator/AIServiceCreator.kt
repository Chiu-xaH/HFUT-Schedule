package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.logic.enumeration.ChatModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AIServiceCreator {
    val client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    private fun getRetrofit(model : ChatModel): Retrofit {
        val baseUrl = model.url

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>, model : ChatModel): T {
        return getRetrofit(model).create(service)
    }

    inline fun <reified T> create(model : ChatModel): T {
        return create(T::class.java, model)
    }
}
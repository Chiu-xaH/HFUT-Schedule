package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.network.core.allowAutoRedirect
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AiServiceCreator {
    val client = OkHttpClient.Builder()
        .allowAutoRedirect(false)
        .build()

    private fun getRetrofit(model : com.hfut.schedule.logic.model.enumeration.ChatModel): Retrofit {
        val baseUrl = model.url

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>, model : com.hfut.schedule.logic.model.enumeration.ChatModel): T {
        return getRetrofit(model).create(service)
    }

    inline fun <reified T> create(model : com.hfut.schedule.logic.model.enumeration.ChatModel): T {
        return create(T::class.java, model)
    }
}
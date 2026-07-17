package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.logic.enumeration.ChatModel
import com.hfut.schedule.network.helper.allowAutoRedirect
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AiServiceCreator {
    val client = OkHttpClient.Builder()
        .allowAutoRedirect(false)
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
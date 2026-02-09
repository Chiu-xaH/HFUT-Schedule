package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.application.MyApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JxglstuServiceCreator {

    val Client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    private fun getRetrofit(useAlternativeUrl: Boolean): Retrofit {
        val baseUrl = if (useAlternativeUrl) {
            MyApplication.Companion.JXGLSTU_WEBVPN_URL
        } else {
            MyApplication.Companion.JXGLSTU_URL
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(Client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>, useAlternativeUrl: Boolean): T {
        return getRetrofit(useAlternativeUrl).create(service)
    }

    inline fun <reified T> create(useAlternativeUrl: Boolean): T {
        return create(T::class.java, useAlternativeUrl)
    }
}
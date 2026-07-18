package com.hfut.schedule.network.api.impl

import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.allowAutoRedirect
import com.hfut.schedule.network.core.timeoutMaxValue
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object JxglstuServiceCreator {
    private const val MAX_WAIT_TIME_SEC = 20L

    val client = OkHttpClient.Builder()
        .allowAutoRedirect(false)
        .timeoutMaxValue(MAX_WAIT_TIME_SEC)
        .build()

    private fun getRetrofit(useAlternativeUrl: Boolean): Retrofit {
        val baseUrl = if (useAlternativeUrl) {
            Constant.JXGLSTU_WEBVPN_URL
        } else {
            Constant.JXGLSTU_URL
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
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
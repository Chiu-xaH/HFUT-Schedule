package com.hfut.schedule.logic.network.servicecreator.Jxglstu

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.network.interceptor.SurveyTokenInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object JxglstuHTMLServiceCreator {
    fun getRetrofit(useAlternativeUrl: Boolean): Retrofit {
        val baseUrl = if (useAlternativeUrl) {
            MyApplication.JXGLSTU_WEBVPN_URL
        } else {
            MyApplication.JXGLSTU_URL
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>, useAlternativeUrl: Boolean): T {
        return getRetrofit(useAlternativeUrl).create(service)
    }

    inline fun <reified T> create(useAlternativeUrl: Boolean): T {
        return create(T::class.java, useAlternativeUrl)
    }
}


object JxglstuJSONServiceCreator {

    val Client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    private fun getRetrofit(useAlternativeUrl: Boolean): Retrofit {
        val baseUrl = if (useAlternativeUrl) {
            MyApplication.JXGLSTU_WEBVPN_URL
        } else {
            MyApplication.JXGLSTU_URL
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

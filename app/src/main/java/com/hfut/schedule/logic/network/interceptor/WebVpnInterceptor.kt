package com.hfut.schedule.logic.network.interceptor

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class WebVpnInterceptor(private val converter: (HttpUrl) -> HttpUrl) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = converter(originalRequest.url)
        val newRequest = originalRequest.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}
/*
val client = OkHttpClient.Builder()
    .addInterceptor(WebVpnInterceptor { url ->
        // 这里调用你已有的转换函数
        convertToWebVpnUrl(url)
    })
    .build()
 */
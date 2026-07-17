package com.hfut.schedule.network.helper

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun OkHttpClient.Builder.timeoutMaxValue(timeSeconds : Long) = this
    .connectTimeout(timeSeconds, TimeUnit.SECONDS)   // 连接超时
    .readTimeout(timeSeconds, TimeUnit.SECONDS)      // 读取超时
    .writeTimeout(timeSeconds, TimeUnit.SECONDS)     // 写入超时


// 允许自动处理302
fun OkHttpClient.Builder.allowAutoRedirect(allow : Boolean) = this
    .followRedirects(allow)
    .followSslRedirects(allow)
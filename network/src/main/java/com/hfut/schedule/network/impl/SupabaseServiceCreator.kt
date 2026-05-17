package com.hfut.schedule.network.impl

import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.util.Constant
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

private const val MAX_TIME = 30L

object SupabaseServiceCreator : BaseServiceCreator(
    Constant.SUPABASE_URL,
    client = OkHttpClient.Builder()
        .connectTimeout(MAX_TIME, TimeUnit.SECONDS)   // 连接超时
        .readTimeout(MAX_TIME, TimeUnit.SECONDS)      // 读取超时
        .writeTimeout(MAX_TIME, TimeUnit.SECONDS)     // 写入超时
        .build()
)
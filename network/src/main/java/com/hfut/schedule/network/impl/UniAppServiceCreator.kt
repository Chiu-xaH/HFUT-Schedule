package com.hfut.schedule.network.impl

import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.util.Constant
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object UniAppServiceCreator : BaseServiceCreator(
    Constant.UNI_APP_URL,
    // 时间宽恕 这个接口服务器太土豆了
    client = OkHttpClient.Builder()
        .connectTimeout(Constant.UNI_APP_MAX_WAIT_TIME_SEC, TimeUnit.SECONDS)   // 连接超时
        .readTimeout(Constant.UNI_APP_MAX_WAIT_TIME_SEC, TimeUnit.SECONDS)      // 读取超时
        .writeTimeout(Constant.UNI_APP_MAX_WAIT_TIME_SEC, TimeUnit.SECONDS)     // 写入超时
        .build()
)
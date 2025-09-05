package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.application.MyApplication
import okhttp3.OkHttpClient

object StuServiceCreator : BaseServiceCreator(
    url = MyApplication.STU_URL,
    client = OkHttpClient.Builder()
        .followRedirects(false)  // 禁止自动处理 302
        .followSslRedirects(false)
        .build()
)
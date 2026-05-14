package com.hfut.schedule.network.impl

import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.util.Constant
import okhttp3.OkHttpClient

object StuServiceCreator : BaseServiceCreator(
    url = Constant.STU_URL,
    client = OkHttpClient.Builder()
        .followRedirects(false)  // 禁止自动处理 302
        .followSslRedirects(false)
        .build()
)
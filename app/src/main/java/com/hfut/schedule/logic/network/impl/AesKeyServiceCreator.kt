package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.logic.network.interceptor.AesKeyInterceptor
import com.hfut.schedule.network.core.BaseServiceCreator
import com.hfut.schedule.network.api.model.Constant
import okhttp3.OkHttpClient

object AesKeyServiceCreator : BaseServiceCreator(
    url = Constant.CAS_LOGIN_URL,
    client = OkHttpClient.Builder()
        .addInterceptor(AesKeyInterceptor())//提取密钥
        .build()
)
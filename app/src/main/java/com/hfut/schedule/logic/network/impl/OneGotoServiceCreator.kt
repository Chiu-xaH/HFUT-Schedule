package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.logic.network.interceptor.GotoInterceptor
import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.util.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OneGotoServiceCreator : BaseServiceCreator(
    url = Constant.CAS_LOGIN_URL,
    client = OkHttpClient.Builder()
            .addNetworkInterceptor(GotoInterceptor())
            .build()
)
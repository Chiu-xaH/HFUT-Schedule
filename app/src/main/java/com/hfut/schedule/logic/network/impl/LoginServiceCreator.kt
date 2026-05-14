package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.logic.network.interceptor.RedirectTicketInterceptor
import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.util.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginServiceCreator : BaseServiceCreator(
    url = Constant.CAS_LOGIN_URL,
    client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .addInterceptor(RedirectTicketInterceptor())
        .build()
)
package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.logic.network.interceptor.GotoInterceptor
import com.hfut.schedule.network.core.BaseServiceCreator
import com.hfut.schedule.network.api.model.Constant
import okhttp3.OkHttpClient

object OneGotoServiceCreator : BaseServiceCreator(
    url = Constant.CAS_LOGIN_URL,
    client = OkHttpClient.Builder()
            .addNetworkInterceptor(GotoInterceptor())
            .build()
)
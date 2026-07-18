package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.logic.network.interceptor.RedirectTicketInterceptor
import com.hfut.schedule.network.core.BaseServiceCreator
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.allowAutoRedirect
import okhttp3.OkHttpClient

object LoginServiceCreator : BaseServiceCreator(
    url = Constant.CAS_LOGIN_URL,
    client = OkHttpClient.Builder()
        .allowAutoRedirect(false)
        .addInterceptor(RedirectTicketInterceptor())
        .build()
)
package com.hfut.schedule.logic.network.impl

import com.hfut.schedule.logic.network.interceptor.RedirectTicketInterceptor
import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.network.util.allowAutoRedirect
import okhttp3.OkHttpClient

object LoginServiceCreator : BaseServiceCreator(
    url = Constant.CAS_LOGIN_URL,
    client = OkHttpClient.Builder()
        .allowAutoRedirect(false)
        .addInterceptor(RedirectTicketInterceptor())
        .build()
)
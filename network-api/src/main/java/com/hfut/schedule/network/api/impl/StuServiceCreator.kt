package com.hfut.schedule.network.api.impl

import com.hfut.schedule.network.core.BaseServiceCreator
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.allowAutoRedirect
import okhttp3.OkHttpClient

object StuServiceCreator : BaseServiceCreator(
    url = Constant.STU_URL,
    client = OkHttpClient.Builder()
        .allowAutoRedirect(false)
        .build()
)
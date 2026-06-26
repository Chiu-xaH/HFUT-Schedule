package com.hfut.schedule.network.impl

import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.network.util.allowAutoRedirect
import okhttp3.OkHttpClient

object StuServiceCreator : BaseServiceCreator(
    url = Constant.STU_URL,
    client = OkHttpClient.Builder()
        .allowAutoRedirect(false)
        .build()
)
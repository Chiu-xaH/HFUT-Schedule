package com.hfut.schedule.network.impl

import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.helper.Constant
import com.hfut.schedule.network.helper.allowAutoRedirect
import okhttp3.OkHttpClient

object StuServiceCreator : BaseServiceCreator(
    url = Constant.STU_URL,
    client = OkHttpClient.Builder()
        .allowAutoRedirect(false)
        .build()
)
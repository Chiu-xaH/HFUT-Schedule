package com.hfut.schedule.network.api.impl

import com.hfut.schedule.network.core.BaseServiceCreator
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.timeoutMaxValue
import okhttp3.OkHttpClient

object UniAppServiceCreator : BaseServiceCreator(
    Constant.UNI_APP_URL,
    // 时间宽恕 这个接口服务器太土豆了
    client = OkHttpClient.Builder()
        .timeoutMaxValue(Constant.UNI_APP_MAX_WAIT_TIME_SEC)
        .build()
)
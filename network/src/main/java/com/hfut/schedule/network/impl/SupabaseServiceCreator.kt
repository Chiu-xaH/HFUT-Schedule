package com.hfut.schedule.network.impl

import com.hfut.schedule.network.impl.base.BaseServiceCreator
import com.hfut.schedule.network.helper.Constant
import com.hfut.schedule.network.helper.timeoutMaxValue
import okhttp3.OkHttpClient

private const val MAX_TIME = 30L

object SupabaseServiceCreator : BaseServiceCreator(
    Constant.SUPABASE_URL,
    client = OkHttpClient.Builder()
        .timeoutMaxValue(MAX_TIME)
        .build()
)
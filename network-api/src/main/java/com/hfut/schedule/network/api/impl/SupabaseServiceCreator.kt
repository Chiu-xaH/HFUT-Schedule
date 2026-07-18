package com.hfut.schedule.network.api.impl

import com.hfut.schedule.network.core.BaseServiceCreator
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.timeoutMaxValue
import okhttp3.OkHttpClient

private const val MAX_TIME = 30L

object SupabaseServiceCreator : BaseServiceCreator(
    Constant.SUPABASE_URL,
    client = OkHttpClient.Builder()
        .timeoutMaxValue(MAX_TIME)
        .build()
)
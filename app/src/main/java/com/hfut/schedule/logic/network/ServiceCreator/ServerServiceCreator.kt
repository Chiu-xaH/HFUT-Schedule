package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.ui.Activity.success.focus.getURL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerServiceCreator : BaseServiceCreator(getURL())
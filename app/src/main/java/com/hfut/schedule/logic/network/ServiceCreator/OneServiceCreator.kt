package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.network.ServiceCreator.BaseServiceCreator
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OneServiceCreator : BaseServiceCreator(MyApplication.OneURL)
package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.App.MyApplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LePaoYunServiceCreator : BaseServiceCreator(MyApplication.LePaoYunURL)
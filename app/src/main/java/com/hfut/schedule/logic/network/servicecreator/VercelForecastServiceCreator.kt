package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.App.MyApplication

object VercelForecastServiceCreator : BaseServiceCreator(url = MyApplication.VERCEL_FORECAST_URL)
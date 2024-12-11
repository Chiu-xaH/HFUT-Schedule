package com.hfut.schedule.logic.network.api

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header

//和风天气
//地区ID 101220101 合肥  101221401 宣城
interface QWeatherService {
    //天气预警
    @GET("warning/now")
    fun getWeatherWarn(locationID : String,@Header("Authorization") authorization : String) : Call<ResponseBody>
    //实时天气
    @GET("weather/now")
    fun getWeather(locationID : String,@Header("Authorization") authorization : String) : Call<ResponseBody>
}
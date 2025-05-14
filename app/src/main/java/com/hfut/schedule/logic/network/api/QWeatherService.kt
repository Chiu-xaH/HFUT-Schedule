package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.ui.screen.home.search.function.life.getLocation
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.getCampus
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

//地区ID 101220101 合肥  101221401 宣城
interface QWeatherService {
    //天气预警
    @GET("warning/now")
    fun getWeatherWarn(
        @Query("location") locationID : String = getLocation(),
        @Header("Authorization") authorization : String = Encrypt.getQWeatherAuth()
    ) : Call<ResponseBody>
    //实时天气
    @GET("weather/now")
    fun getWeather(
        @Query("location") locationID : String = getLocation(),
        @Header("Authorization") authorization : String = Encrypt.getQWeatherAuth()
    ) : Call<ResponseBody>

    //每日天气 day=7 15 30
    @GET("weather/{day}d")
    fun getWeatherDay(
        @Path("day") day : Int = 30,
        @Query("location") locationID : String = getLocation(),
        @Header("Authorization") authorization : String = Encrypt.getQWeatherAuth()
    ) : Call<ResponseBody>

    //小时天气 hour=24 72 168
    @GET("weather/{hour}h")
    fun getWeatherHour(
        @Path("hour") hour : Int = 168,
        @Query("location") locationID : String = getLocation(),
        @Header("Authorization") authorization : String = Encrypt.getQWeatherAuth()
    ) : Call<ResponseBody>

    //分钟降水
    @GET("weather/minutely/5m")
    fun getRain(
        @Query("location") locationID : String = getLocation(),
        @Header("Authorization") authorization : String = Encrypt.getQWeatherAuth()
    ) : Call<ResponseBody>

    //天气指数 type=3 穿衣  6 旅游 9 感冒 11 空调 14 晾晒 16 防晒
    @GET("indices/1d")
    fun getTodayConditions(
        @Query("type") type : Int,
        @Query("location") locationID : String = getLocation(),
        @Header("Authorization") authorization : String = Encrypt.getQWeatherAuth()
    ) : Call<ResponseBody>
}
package com.hfut.schedule.network.api.inf

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SchoolNetSelfService {

    @GET("Self/LoginAction.action")
    fun getLoginPage(): Call<ResponseBody>

    @GET("Self/js/random.js")
    fun getRandomJs(): Call<ResponseBody>

    @GET("Self/RandomCodeAction.action")
    fun getRandomCode(
        @Query("randomNum") randomNum: Double = Math.random()
    ): Call<ResponseBody>

    @POST("Self/LoginAction.action")
    fun loginRaw(
        @Body body: RequestBody,
        @Header("Content-Type") contentType: String = "application/x-www-form-urlencoded",
        @Header("Accept") accept: String = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        @Header("Accept-Language") acceptLanguage: String = "zh-CN,zh;q=0.9,en;q=0.8",
        @Header("Cache-Control") cacheControl: String = "max-age=0",
        @Header("Referer") referer: String = "https://xywzz.hfut.edu.cn:8443/Self/LoginAction.action",
        @Header("Origin") origin: String = "https://xywzz.hfut.edu.cn:8443",
        @Header("Upgrade-Insecure-Requests") upgrade: String = "1",
        @Header("User-Agent") userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36 Edg/144.0.0.0"
    ): Call<ResponseBody>

    @GET("Self/MonthPayAction.action")
    fun getMonthPay(
        @Query("type") type: Int = 1,
        @Query("year") year: Int,
        @Header("Referer") referer: String = "https://xywzz.hfut.edu.cn:8443/Self/LoginAction.action"
    ): Call<ResponseBody>
}

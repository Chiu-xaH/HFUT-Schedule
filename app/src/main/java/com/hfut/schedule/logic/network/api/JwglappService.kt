package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface JwglappService {
    //登录到合工大教务
    @GET("login?service=https://jwglapp.hfut.edu.cn/uniapp/#/pages/applet/pwdLogin")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun LoginJwglapp(@Header("Cookie") Cookie : String) : Call<ResponseBody>

    @GET("token/federation/federatedBindingCas?redirect_uri=https://jwglapp.hfut.edu.cn/uniapp/#/pages/startPage/startPage")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun login(@Query("ticket") ticket : String) : Call<ResponseBody>

}
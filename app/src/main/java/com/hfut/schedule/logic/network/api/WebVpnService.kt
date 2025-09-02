package com.hfut.schedule.logic.network.api

import com.hfut.schedule.application.MyApplication
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface WebVpnService {
    @GET("login?cas_login=true")
    fun getTicket() : Call<ResponseBody>

    @GET("http/77726476706e69737468656265737421f3f652d22f367d44300d8db9d6562d/cas/checkInitParams")
    fun putKey(@Header("Cookie") cookie : String) : Call<ResponseBody>

    @GET("wengine-vpn/cookie?method=get&host=cas.hfut.edu.cn&scheme=http&path=/cas/login")
    fun getKeyWebVpn(@Header("Cookie") cookie : String) : Call<ResponseBody>
    @FormUrlEncoded
    @POST("http/77726476706e69737468656265737421f3f652d22f367d44300d8db9d6562d/cas/login?service=http://jxglstu.hfut.edu.cn/eams5-student/neusoft-sso/login")
    @Headers(MyApplication.PC_UA)
    fun loginWebVpn(@Header("Cookie") cookie : String,
                    @Field("username") username: String,
                    @Field("password") password: String,
                    @Field("execution") execution: String,
                    @Field("_eventId") eventId: String = "submit",
                    @Field("capcha") code : String
    ): Call<ResponseBody>
    //登录WEBVPN
    @GET("http/77726476706e69737468656265737421faef469034247d1e760e9cb8d6502720ede479/eams5-student/neusoft-sso/login")
    @Headers(MyApplication.PC_UA)
    fun loginJxglstu(@Header("Cookie") cookie : String): Call<ResponseBody>
}
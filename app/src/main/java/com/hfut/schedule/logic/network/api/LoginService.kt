package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginService {
//获取AES加密的Key
    @GET("cas/checkInitParams")
    fun getKey() : Call<ResponseBody>

    //获取登录POST所需要的Cookie
    @GET ("cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin")
    fun getCookie() : Call<ResponseBody>

//教务系统登录
    @FormUrlEncoded
    //@Headers("User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    @POST("cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin")
    fun login(
        @Header("Cookie") Cookie : String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("execution") execution: String,
        @Field("_eventId") eventId: String
    ) : Call<ResponseBody>
}
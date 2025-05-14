package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginWebsService {
    //登录校园网
    @FormUrlEncoded
    @POST("0.htm")
    fun loginWeb(
        @Field("DDDDD") username : String,
        @Field("upass") password : String,
        @Field("0MKKey") location : String
    ) : Call<ResponseBody>
    //登录校园网2
    @FormUrlEncoded
    @POST("a30.htm")
    fun loginWeb2(
        @Field("DDDDD") username : String,
        @Field("upass") password : String,
        @Field("0MKKey") location : String
    ) : Call<ResponseBody>
    //登陆后获取流量信息
    @GET("a31.htm")
    fun getInfo() : Call<ResponseBody>
    //注销校园网
    @GET("F.htm")
    fun logoutWeb() : Call<ResponseBody>
}
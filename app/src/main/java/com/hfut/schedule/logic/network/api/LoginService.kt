package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginService {
    //获取AES加密的Key
    @GET("cas/checkInitParams")
    fun getKey() : Call<ResponseBody>

    //获取登录POST所需要的Cookie
    @GET ("cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin")
    fun getCookie() : Call<ResponseBody>

    //教务系统附加AES登录
    @FormUrlEncoded
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    @POST("cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin")
    fun login(
        @Header("Cookie") Cookie : String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("execution") execution: String,
        @Field("_eventId") eventId: String
    ) : Call<ResponseBody>

   //信息门户登录
    @GET("cas/oauth2.0/authorize?response_type=code&client_id=BsHfutEduPortal&redirect_uri=https%3A//one.hfut.edu.cn/home/index")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun OneGoto(
        @Header("Cookie") Cookie : String
    ) : Call<ResponseBody>
    //慧新易校
    @GET("cas/oauth2.0/authorize?response_type=code&client_id=Hfut2023Ydfwpt&redirect_uri=http%3A%2F%2F121.251.19.62%2Fberserker-auth%2Fcas%2Foauth2url%3Foauth2url%3Dhttp%3A%2F%2F121.251.19.62%2Fberserker-base%2Fredirect%3FappId%3D24%26type%3Dapp")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun OneGotoCard(
        @Header("Cookie") Cookie : String
    ) : Call<ResponseBody>




}
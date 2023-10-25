package com.hfut.schedule.logic.network

import okhttp3.Cookie
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
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
//教务系统登录
    @FormUrlEncoded
    //@POST("cas/login?service=https%3A%2F%2Fcas.hfut.edu.cn%2Fcas%2Foauth2.0%2FcallbackAuthorize%3Fclient_id%3DBsHfutEduPortal%26redirect_uri%3Dhttps%253A%252F%252Fone.hfut.edu.cn%252Fhome%252Findex%26response_type%3Dcode%26client_name%3DCasOAuthClient")
    @POST("cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin")
    fun login(@Field("username") username : String,
              @Field("password") password : String,
              @Field("execution") execution : String,
              @Field("_eventId") eventId : String
              )
            : Call<ResponseBody>
}
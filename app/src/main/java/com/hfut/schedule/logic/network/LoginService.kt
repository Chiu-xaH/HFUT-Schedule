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
    @POST("cas/login?service=http%3A%2F%2Fjxglstu.hfut.edu.cn%2Feams5-student%2Fneusoft-sso%2Flogin")
    fun login(@Field("username") username : String,
              @Field("password") password : String,
              )
            : Call<ResponseBody>
}
package com.hfut.schedule.logic.network

import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

object LoginService {
    @GET("eams5-student/neusoft-sso/login")
     fun getCookies() : ResponseBody {
        return TODO("提供返回值")
    }

    @FormUrlEncoded
    @POST("eams5-student/neusoft-sso/login")
    fun login(@Field("username") username : String, @Field("password") password: String)
               : Call<>


}
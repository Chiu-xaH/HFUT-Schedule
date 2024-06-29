package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ServerService {
    //向数据库查询数据
    @GET("get.php")
    fun getData(): Call<ResponseBody>


    @FormUrlEncoded
    @POST("usersData.php")
    fun postUse(@Field("dateTime") dateTime : String,
                @Field("user") user : String,
                @Field("systemVersion") systemVersion : Int,
                @Field("studentID") studentID : String,
                @Field("appVersion") appVersion : String,
                @Field("deviceName") deviceName : String) : Call<ResponseBody>
}
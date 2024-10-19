package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ServerService {
    //向服务器查询聚焦数据
    @GET("get.php")
    fun getData(): Call<ResponseBody>

    //发送用户数据给服务器，用于开发者分析用户使用体验进行优化
    @FormUrlEncoded
    @POST("usersData.php")
    fun postUse(@Field("dateTime") dateTime : String,
                @Field("user") user : String,
                @Field("systemVersion") systemVersion : Int,
                @Field("studentID") studentID : String,
                @Field("appVersion") appVersion : String,
                @Field("deviceName") deviceName : String) : Call<ResponseBody>
    //用户提交反馈到服务器
    @FormUrlEncoded
    @POST("postFeedBack.php")
    fun feedBack(@Field("time") dateTime : String,
                @Field("user") user : String,
                @Field("info") info : String,
                @Field("version") appVersion : String,
                @Field("contact") contact : String?) : Call<ResponseBody>
}
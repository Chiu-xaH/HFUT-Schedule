package com.hfut.manage.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ServerService {
    //向数据库添加数据
    @FormUrlEncoded
    @POST("select.php")
    fun addData(@Field("type") type : String,
                @Field("title") title : String,
                @Field("info") info : String,
                @Field("remark") remark : String,
                @Field("startTime") startTime : String,
                @Field("endTime") endTime : String,

                ): Call<ResponseBody>
    //向数据库删除数据
    @FormUrlEncoded
    @POST("delete.php")
    fun deleteData(@Field("type") type : String,
                   @Field("title") title : String,): Call<ResponseBody>
    //向数据库查询数据
    @GET("/get.php")
    fun getData(): Call<ResponseBody>
}
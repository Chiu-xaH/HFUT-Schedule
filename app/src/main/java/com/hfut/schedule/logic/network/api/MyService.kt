package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MyService {
    //获取接口信息
    @GET("/")
    fun my() : Call<ResponseBody>

    //获取全校培养方案列表
    @GET("/program/{campus}/list.json")
    fun getProgramList(
        @Path("campus") campus : String
    ) : Call<ResponseBody>

    //获取指定培养方案
    @GET("/program/{campus}/programs/{id}.json")
    fun getProgram(
        @Path("id") id : Int,
        @Path("campus") campus : String
    ) : Call<ResponseBody>
}
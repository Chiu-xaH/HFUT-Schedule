package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface MyService {
    //获取个人接口信息
    @GET("/")
    fun my() : Call<ResponseBody>
}
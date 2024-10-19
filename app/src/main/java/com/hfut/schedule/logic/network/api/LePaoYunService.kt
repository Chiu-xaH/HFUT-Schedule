package com.hfut.schedule.logic.network.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.HashMap

interface LePaoYunService {
    //获取已跑里程和打卡点经纬度、健跑要求
    @POST("run/getHomeRunInfo")
    @Headers("isApp: app")
    fun getLePaoYunHome(@Header("token") token : String?) : Call<ResponseBody>
}
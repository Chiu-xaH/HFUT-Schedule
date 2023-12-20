package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LePaoYunService {
    @POST("run/getHomeRunInfo")
    @Headers("isApp: app")
    fun getLePaoYunHome(@Header("token") token : String?) : Call<ResponseBody>
}
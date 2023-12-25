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
    @POST("run/getHomeRunInfo")
    @Headers("isApp: app")
    fun getLePaoYunHome(@Header("token") token : String?) : Call<ResponseBody>

    @POST("run/crsReocordInfoList")
    @Headers("isApp: app")
    fun getRunRecord(@Header("token") token : String?
                     ,@Body requestBody: RequestBody
    ) : Call<ResponseBody>
}
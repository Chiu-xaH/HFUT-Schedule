package com.hfut.schedule.network.api

import com.hfut.schedule.network.model.HaiLeDeviceDetailRequest
import com.hfut.schedule.network.model.HaiLeNearPositionRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HaiLeWashingService {
    @POST("position/nearPosition")
    fun getNearPlaces(@Body body : HaiLeNearPositionRequest) : Call<ResponseBody>

    @POST("position/deviceDetailPage")
    fun getDeviceDetail(@Body body : HaiLeDeviceDetailRequest) : Call<ResponseBody>
}
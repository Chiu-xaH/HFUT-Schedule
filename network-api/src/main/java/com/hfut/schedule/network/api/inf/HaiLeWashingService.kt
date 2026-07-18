package com.hfut.schedule.network.api.inf

import com.hfut.schedule.network.api.model.request.haile.HaiLeDeviceDetailRequest
import com.hfut.schedule.network.api.model.request.haile.HaiLeNearPositionRequest
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
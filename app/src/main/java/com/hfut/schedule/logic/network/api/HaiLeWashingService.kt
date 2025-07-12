package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.model.HaiLeDeviceDetailRequestBody
import com.hfut.schedule.logic.model.HaiLeNearPositionRequestBody
import com.hfut.schedule.logic.model.HaiLeTradeListRequestBody
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface HaiLeWashingService {
    @POST("position/nearPosition")
    fun getNearPlaces(@Body body : HaiLeNearPositionRequestBody) : Call<ResponseBody>

    @POST("position/deviceDetailPage")
    fun getDeviceDetail(@Body body : HaiLeDeviceDetailRequestBody) : Call<ResponseBody>

    @POST("trade/list")
    fun getTradeList(@Body body : HaiLeTradeListRequestBody) : Call<ResponseBody>
}
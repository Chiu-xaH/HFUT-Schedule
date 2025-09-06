package com.hfut.schedule.logic.network.api

import com.xah.shared.model.VercelForecastRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface VercelForecastService {
    @POST("forecast/api/")
    fun getData(
        @Body json : List<VercelForecastRequestBody>
    ) : Call<ResponseBody>
}

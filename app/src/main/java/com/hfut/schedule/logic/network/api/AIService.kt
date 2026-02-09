package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.model.ChatRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface AIService {
    @GET("/")
    @Headers("Content-Type: application/json")
    fun chat(
        @Header("Authorization") auth : String,
        @Body body : ChatRequest
    ) : Call<ResponseBody>
}
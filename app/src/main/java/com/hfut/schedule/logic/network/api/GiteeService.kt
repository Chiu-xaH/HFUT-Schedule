package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface GiteeService {
    @GET("releases/tag/Android")
    fun getUpdate() : Call<ResponseBody>
}
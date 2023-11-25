package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface MyService {
    @GET ("/")
    fun my() : Call<ResponseBody>
}
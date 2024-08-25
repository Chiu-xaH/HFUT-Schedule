package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GiteeService {
    @GET("releases/tag/Android")
    fun getUpdate() : Call<ResponseBody>
    @GET("releases/download/Android/{version}.apk")
    fun download(@Path("version")version : String) : Call<ResponseBody>
}
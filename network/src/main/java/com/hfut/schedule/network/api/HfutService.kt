package com.hfut.schedule.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface HfutService {
    // 各级学院
    @GET("jgsz/yxsz.htm")
    fun getDepartments() : Call<ResponseBody>
}
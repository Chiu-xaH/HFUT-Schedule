package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.util.getPageSize
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WorkService {
    @GET("search/provider_new")
    fun search(
        @Query("start_page") start : Int = 1,
        @Query("token") token : String,
        @Query("keyword") keyword : String? = null,
        @Query("type") type : Int? = null,
        @Query("count") pageSize : Int = getPageSize(),
        @Query("start") page : Int = 1
    ) : Call<ResponseBody>
}
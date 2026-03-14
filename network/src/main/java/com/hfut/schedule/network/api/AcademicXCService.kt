package com.hfut.schedule.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AcademicXCService {
    @GET("/{type}/list{page}.htm")
    fun getNews(
        @Path("type") type : Int,
        @Path("page") page : Int = 1
    ) : Call<ResponseBody>
}
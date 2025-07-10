package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface AcademicService {
    @GET
//        ("tzgg/{type}{page}.htm")
    fun getNews(
        @Url url: String
//        @Path("type") type : String,
//        @Path("page") page : String = ""
    ) : Call<ResponseBody>
}
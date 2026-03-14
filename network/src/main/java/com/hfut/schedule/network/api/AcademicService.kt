package com.hfut.schedule.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface AcademicService {
    @GET
    fun getNews(
        @Url url: String
    ) : Call<ResponseBody>
}
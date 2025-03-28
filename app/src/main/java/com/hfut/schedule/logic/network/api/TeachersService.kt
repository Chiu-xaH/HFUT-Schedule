package com.hfut.schedule.logic.network.api

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface TeachersService {
    @GET("system/resource/tsites/advancesearch.jsp")
    fun searchTeacher(
        @Query("teacherName") name : String = "",
        @Query("pagesize") size : String,
        @Query("pageindex") page : Int = 1,
        @Query("showlang") language : String = "zh_CN",
        @Query("searchDirection") direction : String = ""
    ) : Call<ResponseBody>
}
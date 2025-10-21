package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.util.network.getPageSize
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface TeachersService {
    @GET("system/resource/tsites/advancesearch.jsp")
    fun searchTeacher(
        @Query("teacherName") name : String = "",
        @Query("pagesize") size : Int = getPageSize(),
        @Query("pageindex") page : Int = 1,
        @Query("showlang") language : String = "zh_CN",
        @Query("searchDirection") direction : String = "",
        @Query("tutorType") tutorType : String = "",
        @Query("viewid") viewid : String = "1034634",
        @Query("productType") productType : String = "0"
    ) : Call<ResponseBody>
}
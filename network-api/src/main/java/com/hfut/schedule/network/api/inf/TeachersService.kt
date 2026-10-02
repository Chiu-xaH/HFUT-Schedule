package com.hfut.schedule.network.api.inf

import com.hfut.schedule.network.api.model.Constant
import com.xah.common.logic.util.EMPTY_STRING
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TeachersService {
    @GET("system/resource/tsites/advancesearch.jsp")
    fun searchTeacher(
        @Query("teacherName") name : String = EMPTY_STRING,
        @Query("pagesize") size : Int = Constant.DEFAULT_PAGE_SIZE,
        @Query("pageindex") page : Int = 1,
        @Query("showlang") language : String = "zh_CN",
        @Query("searchDirection") direction : String = EMPTY_STRING,
        @Query("tutorType") tutorType : String = EMPTY_STRING,
        @Query("viewid") viewid : String = "1034634",
        @Query("productType") productType : String = "0"
    ) : Call<ResponseBody>
}
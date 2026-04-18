package com.hfut.schedule.network.api

import com.hfut.schedule.network.util.Constant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SecondClassService {
    @GET("scReports/uccp_index")
    fun checkLogin(
        @Header("Cookie") cookie : String
    ) : Call<ResponseBody>

    // 志愿活动
    @GET("scReports/activity/activityPage")
    fun getActivities(
        @Header("Cookie") cookie : String,
        @Query("pageNo") page : Int,
        @Query("pageSize") pageSize : Int = Constant.DEFAULT_PAGE_SIZE,
    ) : Call<ResponseBody>
}
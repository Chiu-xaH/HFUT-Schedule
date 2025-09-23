package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.util.getPageSize
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface OfficeHallService {
    @GET("api/aggr/aggrlightapp/lightappOrderByInitials")
    fun search(
        @Query("code") code : String = "PC_SH_v2_ANO",
        @Query("returnType") type : Int = 4,
        @Query("name") name : String = "",
        @Query("current") page : Int,
        @Query("size") pageSize : Int = getPageSize(),
    ) : Call<ResponseBody>
}
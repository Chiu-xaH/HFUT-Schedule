package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ZhiJianService {
    @POST("api/esb/oa/execute/TB_KBSJCL")
    fun getCourses(
        @Header("Cookie") session : String,
        @Query("params") params : String
    ) : Call<ResponseBody>

    @GET("api/ecode/sync")
    fun checkLogin(
        @Header("Cookie") session : String,
    ) : Call<ResponseBody>
//    @GET("wui/cas-entrance.jsp")
//    fun login(
//        @Query("ticket") ticket : String,
//        @Query("path") path : String = "https%3A%2F%2Fzjgd.hfut.edu.cn%3A8181%2Fwui%2Findex.html%23%2Fmain",
//        @Query("ssoType") type : String = "CAS"
//    )
}
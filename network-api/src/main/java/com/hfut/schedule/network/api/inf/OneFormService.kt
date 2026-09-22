package com.hfut.schedule.network.api.inf

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OneFormService {
    @GET("api/auth/oauth/getToken?type=form")
    fun getToken(
        @Query("redirect") redirect: String,
        @Query("code") code: String
    ): Call<ResponseBody>

    @GET("api/generalform-stu/grcg/cgml/getStucgml")
    fun getStudentAchievement(
        @Header("Authorization") authorization: String,
        @Query("cgType") cgType: String = "",
        @Query("type") type: Int = 2,
        @Query("startNd") startYear: String = "",
        @Query("endNd") endYear: String = ""
    ): Call<ResponseBody>
}

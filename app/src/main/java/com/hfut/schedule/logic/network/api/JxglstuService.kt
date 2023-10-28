package com.hfut.schedule.logic.network.api

import android.telecom.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface JxglstuService {
    @GET("/")
    @Headers("User-Agent:XXX")
    fun Cookieget() : retrofit2.Call<ResponseBody>

    @POST("eams5-student/ws/schedule-table/datum")
    fun getDatum()
}
package com.hfut.schedule.logic.network.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface JxglstuService {
    @GET("/")
    @Headers("User-Agent:XXX")
    fun Cookieget()

    @POST("eams5-student/ws/schedule-table/datum")
    fun getDatum()
}
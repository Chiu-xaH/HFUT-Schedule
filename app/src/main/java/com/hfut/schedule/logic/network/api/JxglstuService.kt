package com.hfut.schedule.logic.network.api

import retrofit2.http.POST

interface JxglstuService {
    @POST("eams5-student/ws/schedule-table/datum")
    fun getDatum()
}
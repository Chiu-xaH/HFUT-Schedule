package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface JxglstuService {

    //获取Cookie，带着Cookie访问下一个课程表
    @Headers("Cookie:SRVID=s114",
        "User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17"
       )
    @GET("eams5-student/neusoft-sso/login{ticket}")
    fun Cookieget(@Path("ticket") ticket : String) : retrofit2.Call<ResponseBody>

    //课程表JSON
    @POST("eams5-student/ws/schedule-table/datum")
    fun getDatum()
}



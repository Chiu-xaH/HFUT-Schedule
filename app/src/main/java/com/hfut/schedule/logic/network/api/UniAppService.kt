package com.hfut.schedule.logic.network.api

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// 合工大教务
interface UniAppService {
    // 登录
    @POST("token/password/passwordLogin")
    fun login(
        @Query("username") studentId : String,
        @Query("password") password : String,
        @Query("appId") appId : String = "APP_ID",
        @Query("deviceId") deviceId : String = "DEVICE_ID"
    ) : Call<ResponseBody>
    // 教室课表
    // 空教室
    // 同班同学
    @POST("eams-micro-server/api/v1/lesson/student/class-mates/{id}")
    fun getClassmates(
        @Path("id") lessonId : String,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 全校培养方案
    // 成绩
    // 考试
    // 课表
}
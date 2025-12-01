package com.hfut.schedule.logic.network.api

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.GET
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
    // 同班同学
    @GET("eams-micro-server/api/v1/lesson/student/class-mates/{id}")
    fun getClassmates(
        @Path("id") lessonId : String,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 成绩
    @GET("eams-micro-server/api/v1/grade/student/grades")
    fun getGrades(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 考试
    @GET("eams-micro-server/api/v1/exam/student/exam")
    fun getExams(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 课表
    @GET("eams-micro-server/api/v1/lesson/student/course-table/{semseter}")
    fun getCourses(
        @Path("semseter") semseter : Int,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 教室课表
    // TODO
    // 空教室
    // TODO
    // 全校培养方案
    // TODO
}
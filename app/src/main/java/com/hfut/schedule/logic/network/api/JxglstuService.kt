package com.hfut.schedule.logic.network.api

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


interface JxglstuService {
    //带着Cookie登录教务系统主页
    @GET("neusoft-sso/login")
    @Headers("User-Agent:MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
    fun jxglstulogin(@Header("Cookie") Cookie : String) : Call<ResponseBody>

    //获取学生Id
    @GET("for-std/course-table")
    @Headers("User-Agent:MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
    fun getStudentId(@Header("Cookie") Cookie : String) : Call<ResponseBody>

    //获取需要POST的lessonsId
    //eams5-student/for-std/course-table/get-data?bizTypeId=23&semesterId=234&dataId=170317
    @GET("for-std/course-table/get-data?semesterId=234")
    @Headers("User-Agent:MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
    fun getLessonIds(@Header("Cookie") Cookie : String,
                     @Query("bizTypeId") bizTypeId : String,
                    // @Query("semesterId") semesterId : String,
                     @Query("dataId") dataId : String
                     ) : Call<ResponseBody>

    //获取课程表JSON
    @POST("ws/schedule-table/datum")
    @Headers("User-Agent:MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
        "Content-Type: application/json")
    fun getDatum(@Header("Cookie") Cookie : String,
                 @Body json: JsonObject
    ) : Call<ResponseBody>
}



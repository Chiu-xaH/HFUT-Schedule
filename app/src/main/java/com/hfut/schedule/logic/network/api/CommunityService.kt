package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface CommunityService {

    //社区登录
    @GET("cas/login?service=https%3A%2F%2Fcommunity.hfut.edu.cn%2F")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun LoginCommunity(@Header("Cookie") Cookie : String) : Call<ResponseBody>

    @GET("api/sys/cas/client/validateLogin?service=https:%2F%2Fcommunity.hfut.edu.cn%2F")
    fun Login(@Query("ticket") ticket : String) : Call<ResponseBody>

    @GET("api/business/score/querytotalscore")
    fun getAvgGrade(@Header("X-Access-Token") Token : String) : Call<ResponseBody>

    @GET("api/business/score/scoreselect")
    fun getGrade(@Header("X-Access-Token") Token : String,
                 @Query("xn") year : String,
                 @Query("xq") term : String) : Call<ResponseBody>
    @GET("api/business/score/querymyprogress")
    fun getAllAvgGrade(@Header("X-Access-Token") Token : String) : Call<ResponseBody>
    @GET("api/business/coursefailrate/list")
    fun getFailRate(
        @Header("X-Access-Token") Token : String,
        @Query("courseName") courseName : String,
        @Query("pageNo") page : String
        ,@Query("pageSize") size : String
    ) : Call<ResponseBody>

    @GET("api/business/book/search")
    fun searchBooks(
        @Header("X-Access-Token") Token : String,
        @Query("name") name: String,
        @Query("pageNo") page : String
        ,@Query("pageSize") size : String
    ) : Call<ResponseBody>

    @GET("api/business/examarrangement/listselect")
    fun getExam(@Header("X-Access-Token") Token : String) : Call<ResponseBody>

    @GET("api/business/book/lending/lendingList?name=")
    fun getBorrowedBook(
        @Header("X-Access-Token") Token : String,
        @Query("pageNo") page : String,
        @Query("pageSize") size : String) : Call<ResponseBody>

    @GET("api/business/book/lending/historyList?name=")
    fun getHistoyBook(
        @Header("X-Access-Token") Token : String,
        @Query("pageNo") page : String,
        @Query("pageSize") size : String) : Call<ResponseBody>

    @GET("api/business/book/lending/overdueList?name=")
    fun getOverDueBook(
        @Header("X-Access-Token") Token : String,
        @Query("pageNo") page : String,
        @Query("pageSize") size : String) : Call<ResponseBody>

    @GET("api/business/coursearrangement/listselect")
    fun getCourse(@Header("X-Access-Token") Token : String) : Call<ResponseBody>

    @GET("api/mobile/community/homePage/today")
    fun getToday(@Header("X-Access-Token") Token : String) : Call<ResponseBody>
}
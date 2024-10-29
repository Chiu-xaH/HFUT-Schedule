package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GuaGuaService {
    //登录
    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("telPhone") phoneNumber: String,
        @Field("password") password: String,
        @Field("typeId") typeId: Int = 0
    ) : Call<ResponseBody>
    //获取使用码后面数字
    @GET("user/useCode")
    fun getUseCode(
        @Query("telPhone") phoneNumber : String,
        @Query("loginCode") loginCode : String,
    ): Call<ResponseBody>
    //账单
    @GET("wallet/billList")
    fun getBills(
        @Query("telPhone") phoneNumber : String,
        @Query("loginCode") loginCode : String,
        @Query("curNum") page : Int = 1,
        @Query("typeId") typeId: Int = 0,
        //YYYY-MM-DD HH:MM:SS
        @Query("beginDate") startDate : String = "2023-10-01 00:00:00",
        @Query("endDate") endDate : String = "2050/12/31 23:59:59",
    ): Call<ResponseBody>
    //开始洗浴
    @FormUrlEncoded
    @POST("order/downRate/snWater")
    fun startShower(
        @Field("telPhone") phoneNumber: String,
        @Field("loginCode") loginCode: String,
        @Field("deviceSncode") macLocation : String,
        @Field("appId") appId : String = "10010"
    ) : Call<ResponseBody>
    //用户信息
    @GET("user/info")
    fun getUserInfo(
        @Query("telPhone") phoneNumber : String,
        @Query("loginCode") loginCode : String
    ) : Call<ResponseBody>
}
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
    )
    //账单
    @GET("wallet/billList")
    fun getBills(
        @Query("telPhone") phoneNumber : String,
        @Query("loginCode") loginCode : String,
        @Query("curNum") page : Int = 1,
        @Query("typeId") typeId: Int = 0,
        //YYYY-MM-DD HH:MM:SS
        @Query("beginDate") startDate : String,
        @Query("endDate") endDate : String,
    )
}
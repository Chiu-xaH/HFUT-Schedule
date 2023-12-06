package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface FWDTService {
    @FormUrlEncoded
    @POST ("Login/LoginBySnoQuery")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun loginFWDT(
        @Field("sno") sno : String,
        @Field("pwd") pwd : String,
        @Field("ValiCode") code : String,
        @Field("remember") remember : String,
        @Field("uclass") uclass : String,
        @Field("json") json : Boolean,
    ) : Call<ResponseBody>
}
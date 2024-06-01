package com.hfut.schedule.logic.network.api

import android.telecom.Call
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface loginWebService {
    @FormUrlEncoded
    @POST("0.htm")
    fun loginWeb(
        @Field("DDDDD") username : String,
        @Field("upass") password : String,
        @Field("0MKKey") location : String,
        @Field("v6ip") v6ip : String?
    ) : retrofit2.Call<ResponseBody>
}
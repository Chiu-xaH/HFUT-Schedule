package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface FWDTService {
    @FormUrlEncoded
    @POST("web/Common/Tsm.html")
    fun searchEle(@Field("jsondata") jsondata : String,@Field("funname") funname : String,@Field("json") json : Boolean) : Call<ResponseBody>
}
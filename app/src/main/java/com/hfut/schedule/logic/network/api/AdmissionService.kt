package com.hfut.schedule.logic.network.api

import com.hfut.schedule.application.MyApplication
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AdmissionService {
    // 列表
    @POST("f/ajax_{type}_param")
    @Headers(
        "X-Requested-With: XMLHttpRequest",
        "X-Requested-Time: 1",
        "Referer: ${MyApplication.UNDERGRADUATE_ADMISSION_URL}"
    )
    fun getList(@Path("type") type: String) : Call<ResponseBody>
    // 详情
    @Headers(
        "X-Requested-With: XMLHttpRequest",
        "X-Requested-Time: ",
        "Referer: ${MyApplication.UNDERGRADUATE_ADMISSION_URL}"
    )
    @FormUrlEncoded
    @POST("f/ajax_{type}")
    fun getDetail(
        @Path("type") type: String,
        @Field("ssmc") region : String,
        @Field("zsnf") year : String,
        @Field("klmc") subject : String,
        @Field("campus") campus : String,
        @Field("zslx") postType : String,
        @Header("Cookie") cookie : String,
        @Header("Csrf-Token") token : String
    ) : Call<ResponseBody>

    @POST("f/ajax_get_csrfToken")
    @FormUrlEncoded
    @Headers(
        "X-Requested-With: XMLHttpRequest",
        "X-Requested-Time: ",
        "Referer: ${MyApplication.UNDERGRADUATE_ADMISSION_URL}"
    )
    fun getToken(
        @Field("n") count : Int = 1,
        @Header("Cookie") cookie : String = ""
    ) : Call<ResponseBody>
}
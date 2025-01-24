package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface XuanChengService {
    //宣区通知公告
    @GET("1955/list{page}.htm")
    fun getNotications(@Path("page") page : String = "") : Call<ResponseBody>

    //检索通知公告
    @FormUrlEncoded
    @POST("_web/_search/api/searchCon/create.rst?_p=YXM9MiZ0PTE0NTcmZD0zODcxJnA9MiZmPTEmbT1TTiZ8Ym5uQ29sdW1uVmlydHVhbE5hbWU9MS0m")
    fun searchNotications(@Field("searchInfo") text : String) : Call<ResponseBody>
}
package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NewsService {
    //获取新闻通知
    @FormUrlEncoded
    @POST("zq_search.jsp?wbtreeid=1137")
    fun searchNews(@Field("sitenewskeycode") sitenewskeycode : String,@Field("currentnum") page : Int = 1) : Call<ResponseBody>
}
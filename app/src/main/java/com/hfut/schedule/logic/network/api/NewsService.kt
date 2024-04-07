package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NewsService {
    @FormUrlEncoded
    @POST("zq_search.jsp?wbtreeid=1001")
    fun searchNews(@Field("searchScope") searchScope : Int, @Field("sitenewskeycode") sitenewskeycode : String) : Call<ResponseBody>
}
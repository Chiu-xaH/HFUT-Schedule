package com.hfut.schedule.logic.network.api

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MyService {
    @GET ("/")
    fun my() : Call<ResponseBody>

    @POST ("opac/ajax_search_adv.php")
    @Headers("Accept: application/json, text/javascript, */*; q=0.01")
    fun LibSearch(@Body json: JsonObject) : Call<ResponseBody>

    @GET ("opac/presearch.php")
    @Headers("Accept: application/json, text/javascript, */*; q=0.01")
    fun LibSearch2() : Call<ResponseBody>
}
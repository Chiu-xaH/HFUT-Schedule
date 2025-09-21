package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LibraryService {
    @GET("svc/control/currentTenantUser/loginUserInfo")
    fun checkLogin(
        @Header("authorization") auth : String
    ) : Call<ResponseBody>

    @POST("svc/circulate/myLibCall/listMyLibStatis")
    fun getStatus(
        @Body body : Map<String, String> = mapOf(),
        @Header("authorization") auth : String
    ) : Call<ResponseBody>

//    @GET("svc/circulate/readerRecord")
//    fun getBorrowedRecord(
//        @Header("authorization") auth : String,
//        @Query("page") page : Int,
//        @Query("pageSize") pageSize : Int,
//
//    )

    @POST("svc/space/mate/search")
    fun search(
//        @Body body : ,
        @Header("authorization") auth : String

    )
}
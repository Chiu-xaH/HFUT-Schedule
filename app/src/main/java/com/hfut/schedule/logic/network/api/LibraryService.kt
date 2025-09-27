package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.util.getPageSize
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LibraryService {
    @GET("/")
    fun check() : Call<ResponseBody>

    @GET("svc/control/currentTenantUser/loginUserInfo")
    fun checkLogin(
        @Header("authorization") auth : String
    ) : Call<ResponseBody>

    @POST("svc/circulate/myLibCall/listMyLibStatis")
    fun getStatus(
        @Body body : Map<String, String> = mapOf(),
        @Header("authorization") auth : String
    ) : Call<ResponseBody>

    @GET("svc/circulate/readerRecord")
    fun getBorrowed(
        @Header("authorization") auth : String,
        @Query("page") page : Int,
        @Query("status") status : String?,
        @Query("limit") pageSize : Int = getPageSize(),
    ) : Call<ResponseBody>

    data class BookShelfRequest(val page: Int,val size : Int = getPageSize())
    @POST("svc/circulate/bookShelf/query")
    fun getBookShelf(
        @Header("authorization") auth : String,
        @Body bean : BookShelfRequest
    ) : Call<ResponseBody>

    data class BookCollectRequest(val page: Int,val limit : Int = getPageSize(),val sorts : List<String> = emptyList())
    @POST("svc/circulate/myCollect/listMyCollect")
    fun getMyCollect(
        @Header("authorization") auth : String,
        @Body bean : BookCollectRequest
    ) : Call<ResponseBody>


    @POST("svc/space/mate/search")
    fun search(
        @Header("authorization") auth : String
    ) : Call<ResponseBody>
}
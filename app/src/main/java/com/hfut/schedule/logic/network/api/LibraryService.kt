package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.util.network.getPageSize
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
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


    data class BookSearchRequest(val page : Int, val conditions : List<BookSearchRequestKeywordBean>, val source : BookSearchRequestSourceBean?, val size : Int = getPageSize(), val sort : Int = 0)
    data class BookSearchRequestKeywordBean(val value : String)
    data class BookSearchRequestSourceBean(val Cats : List<String>)
    @POST("svc/space/mate/search")
    @Headers("Content-Type: application/json")
    fun search(
        @Header("authorization") auth : String,
        @Body body : BookSearchRequest
    ) : Call<ResponseBody>
}
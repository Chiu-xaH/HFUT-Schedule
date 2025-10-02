package com.hfut.schedule.logic.network.api

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CasLoginType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    //获取AES加密的Key
    @GET("cas/checkInitParams")
    fun getKey() : Call<ResponseBody>

    //获取登录POST所需要的Cookie
    @GET ("cas/login")
    fun getCookie(
        @Query("service") url : String? = CasLoginType.JXGLSTU.service
    ) : Call<ResponseBody>

    //教务系统附加AES登录
    @FormUrlEncoded
    @Headers(MyApplication.PC_UA)
    @POST("cas/login")
    fun loginCas(
        @Header("Cookie") cookie : String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("execution") execution: String,
        @Field("_eventId") eventId: String = "submit",
        @Field("capcha") code : String,
        @Query("service") url : String? = CasLoginType.JXGLSTU.service
    ) : Call<ResponseBody>

    // 待重构 这个接口可以供教务、信息门户、学工系统等登录，传入不同的serviceURL，但是早期不会这些，写的时候没解耦合，懒得重构了，等有空的
    @GET("cas/login")
    @Headers(MyApplication.PC_UA)
    fun loginGoTo(
        @Query("service") service : String?,
        @Header("Cookie") cookie : String
    ) : Call<ResponseBody>

    @GET("cas/oauth2.0/authorize")
    @Headers(MyApplication.PC_UA)
    fun loginGoToOauth(
        @Query("client_id") clientId : String,
        @Query("redirect_uri") url : String,
        @Header("Cookie") cookiesWithTgc : String,
        @Query("response_type") code : String = "code",
    ) : Call<ResponseBody>


    @GET("cas/password/getContactInfo")
    fun queryExist(
        @Query("username") username : String
    ) : Call<ResponseBody>

    @GET("cas/app/query")
    fun queryVerified(
        @Query("uuid") uuid : String
    ) : Call<ResponseBody> // VERIFIED

    @GET("cas/app/qrCode")
    fun postUuid(
        @Query("uuid") uuid : String
    ) : Call<ResponseBody>// success

    @GET("cas/app/saveTGC")
    fun getTgc(
        @Query("uuid") uuid : String
    ) : Call<ResponseBody> // Set-Cookie//TGC=

    // 用TGC=XX作为Cookie，发送loginGoTo或loginGoToOauth登录其他平台



}
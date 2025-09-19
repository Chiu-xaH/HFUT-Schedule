package com.hfut.schedule.logic.network.api

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.model.SupabaseEventEntity
import com.hfut.schedule.logic.model.SupabaseEventForkCount
import com.hfut.schedule.logic.model.SupabaseEventForkEntity
import com.hfut.schedule.logic.model.SupabaseUsageEntity
import com.hfut.schedule.logic.model.SupabaseUserLoginBean
import com.hfut.schedule.logic.util.network.Encrypt.getSupabasePublicKey
import com.hfut.schedule.logic.network.util.toDateTimeBeanForSupabase
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getEventCampus
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseService {
    // 注册
    @POST("auth/v1/signup")
    fun reg(
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Body user : SupabaseUserLoginBean
    ) : Call<ResponseBody>

    // 登录
    @POST("auth/v1/token")
    fun login(
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Query("grant_type") loginType : String,
//        @Header("Authorization") authorization : String? = null,
        @Body user : Any
    ) : Call<ResponseBody>

    // 验证token
    @GET("functions/v1/check-login")
    fun checkToken(
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Header("Authorization") authorization : String,
    ) : Call<ResponseBody>


    // EVENT查
    @GET("rest/v1/event")
    fun getEvents(
//        @Header("Authorization") authorization : String,
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Query("end_time") endTime : String? = "gt.${toDateTimeBeanForSupabase()}",
        @Query("contributor_email") email : String? = null,
    ) : Call<ResponseBody>

    // EVENT删
    @DELETE("rest/v1/event")
    fun delEvent(
        @Header("Authorization") authorization : String,
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Query("id") id : String
    ) : Call<ResponseBody>

    // EVENT增
    @POST("rest/v1/event")
    fun addEvent(
        @Header("Authorization") authorization : String,
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Header("Prefer") prefer: String = "return=representation",
        @Body entity : SupabaseEventEntity
    ) : Call<ResponseBody>

    // EVENT改
    @PATCH("rest/v1/event")
    fun updateEvent(
        @Header("Authorization") authorization : String,
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Query("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ) : Call<ResponseBody>

    // EVENT下载+1
    @POST("rest/v1/event_fork")
    fun eventDownloadAdd(
        @Header("Authorization") authorization : String,
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Body entity : SupabaseEventForkEntity
    ) : Call<ResponseBody>

    // EVENT下载数量
    @POST("rest/v1/rpc/get_event_fork_count")
    fun getEventDownloadCount(
        @Header("Authorization") authorization : String,
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Body entity : SupabaseEventForkCount
    ) : Call<ResponseBody>

    // EVENT日程数量
    data class SupabaseEventCount(
        @SerializedName("class_text")
        val classes : String? = getPersonInfo().className,
        @SerializedName("campus_text")
        val campus : String = getEventCampus().name,
        @SerializedName("end_time_param")
        val endTime : String = toDateTimeBeanForSupabase()
    )
    @POST("rest/v1/rpc/get_event_count")
    fun getEventCount(
        @Header("Authorization") authorization : String,
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Body entity : SupabaseEventCount = SupabaseEventCount()
    ) : Call<ResponseBody>

    data class SupabaseEventLatest(
        @SerializedName("class_text")
        val classes : String? = getPersonInfo().className,
        @SerializedName("campus_text")
        val campus : String = getEventCampus().name,
    )
    @POST("rest/v1/rpc/get_latest_event_end_time")
    fun getEventLatestTime(
        @Header("Authorization") authorization : String,
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Body entity : SupabaseEventLatest = SupabaseEventLatest()
    ) : Call<ResponseBody>

    //发送用户数据给服务器，用于开发者分析用户使用体验进行优化
    @POST("rest/v1/user_app_usage")
    fun postUsage(
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
        @Body entity : SupabaseUsageEntity = SupabaseUsageEntity()
    ) : Call<ResponseBody>

    // 查询今日访问量
    @POST("rest/v1/rpc/get_today_visit_count")
    fun getTodayVisitCount(
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
    ) : Call<ResponseBody>
    // 查询用户总数
    @POST("rest/v1/rpc/get_user_count")
    fun getUserCount(
        @Header("apikey") publicKey : String = getSupabasePublicKey(),
    ) : Call<ResponseBody>
}
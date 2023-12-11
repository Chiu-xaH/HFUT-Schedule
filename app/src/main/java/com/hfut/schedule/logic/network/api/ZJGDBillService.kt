package com.hfut.schedule.logic.network.api

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ZJGDBillService {
    @GET("berserker-app/ykt/tsm/getCampusCards?synAccessSource=h5")
    @Headers("User-Agent: User-Agent: Mozilla/5.0 (Linux; Android 10; V1821A Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/106.0.5249.126 Mobile Safari/537.36 E-Mobile7/7.0.48.20230505 Language/zh Qiyuesuo/physicalSDK","X-Requested-With: com.weaver.custom7","Upgrade-Insecure-Requests: 1")
    fun getYue(@Header("synjones-auth") auth : String, ) : Call<ResponseBody>


    @GET("berserker-search/search/personal/turnover?size=8&synAccessSource=h5")
    @Headers("User-Agent: User-Agent: Mozilla/5.0 (Linux; Android 10; V1821A Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/106.0.5249.126 Mobile Safari/537.36 E-Mobile7/7.0.48.20230505 Language/zh Qiyuesuo/physicalSDK","X-Requested-With: com.weaver.custom7","Upgrade-Insecure-Requests: 1")
    fun Cardget(@Header("synjones-auth") auth : String,
                @Query("current") page : Int) : Call<ResponseBody>

    //查询一个月每天总消费
    @GET("berserker-search/statistics/turnover/sum/user?dateType=month&statisticsDateStr=day&type=2&synAccessSource=h5")
    @Headers("User-Agent: User-Agent: Mozilla/5.0 (Linux; Android 10; V1821A Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/106.0.5249.126 Mobile Safari/537.36 E-Mobile7/7.0.48.20230505 Language/zh Qiyuesuo/physicalSDK","X-Requested-With: com.weaver.custom7","Upgrade-Insecure-Requests: 1")
    fun getMonthYue(@Header("synjones-auth") auth : String,
                    @Query("dateStr") dateStr : String ) : Call<ResponseBody>

   //查询从哪天到哪天的消费
    @GET("berserker-search/statistics/turnover/count?synAccessSource=h5")
    @Headers("User-Agent: User-Agent: Mozilla/5.0 (Linux; Android 10; V1821A Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/106.0.5249.126 Mobile Safari/537.36 E-Mobile7/7.0.48.20230505 Language/zh Qiyuesuo/physicalSDK","X-Requested-With: com.weaver.custom7","Upgrade-Insecure-Requests: 1")
    fun searchDate(@Header("synjones-auth") auth : String,
                   @Query("timeFrom") timeForm : String,
                   @Query("timeTo") timeTo : String ) : Call<ResponseBody>

    //查询关键字
    @GET("berserker-search/search/personal/turnover?size=10&highlightFieldsClass=text-primary&synAccessSource=h5")
    @Headers("User-Agent: User-Agent: Mozilla/5.0 (Linux; Android 10; V1821A Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/106.0.5249.126 Mobile Safari/537.36 E-Mobile7/7.0.48.20230505 Language/zh Qiyuesuo/physicalSDK","X-Requested-With: com.weaver.custom7","Upgrade-Insecure-Requests: 1")
    fun searchBills(@Header("synjones-auth") auth : String,
                   @Query("info") info : String,
                    @Query("current") page : Int) : Call<ResponseBody>


    //修改限额
    @POST("berserker-app/ykt/tsm/modifyAcc")
    @Headers("User-Agent: User-Agent: Mozilla/5.0 (Linux; Android 10; V1821A Build/QP1A.190711.020; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/106.0.5249.126 Mobile Safari/537.36 E-Mobile7/7.0.48.20230505 Language/zh Qiyuesuo/physicalSDK","X-Requested-With: com.weaver.custom7","Upgrade-Insecure-Requests: 1")
    fun changeLimit(
        @Header("synjones-auth") auth : String,
        @Body json: JsonObject ) : Call<ResponseBody>
}
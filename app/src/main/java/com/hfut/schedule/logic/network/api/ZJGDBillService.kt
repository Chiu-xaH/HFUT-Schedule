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
    fun getYue(@Header("synjones-auth") auth : String, ) : Call<ResponseBody>


    @GET("berserker-search/search/personal/turnover?synAccessSource=h5")
    fun Cardget(@Header("synjones-auth") auth : String,
                @Query("current") page : Int
                ,@Query("size") size : String) : Call<ResponseBody>

    //查询一个月每天总消费
    @GET("berserker-search/statistics/turnover/sum/user?dateType=month&statisticsDateStr=day&type=2&synAccessSource=h5")
    fun getMonthYue(@Header("synjones-auth") auth : String,
                    @Query("dateStr") dateStr : String ) : Call<ResponseBody>

   //查询从哪天到哪天的消费
    @GET("berserker-search/statistics/turnover/count?synAccessSource=h5")
    fun searchDate(@Header("synjones-auth") auth : String,
                   @Query("timeFrom") timeForm : String,
                   @Query("timeTo") timeTo : String ) : Call<ResponseBody>

    //查询关键字
    @GET("berserker-search/search/personal/turnover?highlightFieldsClass=text-primary&synAccessSource=h5")
    fun searchBills(@Header("synjones-auth") auth : String,
                   @Query("info") info : String,
                    @Query("current") page : Int
        ,@Query("size") size : String) : Call<ResponseBody>


    //修改限额
    @POST("berserker-app/ykt/tsm/modifyAcc")
    fun changeLimit(
        @Header("synjones-auth") auth : String,
        @Body json: JsonObject ) : Call<ResponseBody>

    //查询  feeitemid为281为网费，261为电费
    @FormUrlEncoded
    @POST("charge/feeitem/getThirdData")
    fun getFee(@Header("synjones-auth") auth : String,
               @Field("feeitemid") typeId : String,
               @Field("type") IEC : String,
               @Field("level") level : String?,
               @Field("room") room : String?) : Call<ResponseBody>
    //交费支付 电费 网费
    //@POST("blade-pay/pay")
    //查询限额

    //付款码

    //挂失解挂

    //状态获取

}
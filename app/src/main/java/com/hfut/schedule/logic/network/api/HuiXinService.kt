package com.hfut.schedule.logic.network.api

import com.google.gson.JsonObject
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface HuiXinService {
    // 登录
    @FormUrlEncoded
    @POST("berserker-auth/oauth/token")
    fun login(
        @Field("username") studentId : String,
        @Field("password") password : String,
        @Field("grant_type") type : String = "password",
        @Field("logintype") loginType : String = "sno",
        @Header("Authorization") auth : String = Crypto.getHuiXinAuth(),
    ) : Call<ResponseBody>
    //
    @GET("berserker-app/ykt/tsm/getCampusCards")
    fun checkLogin(@Header("synjones-auth") auth : String ) : Call<ResponseBody>
    //一卡通基本信息 其中包括余额
    @GET("berserker-app/ykt/tsm/getCampusCards")
    fun getYue(@Header("synjones-auth") auth : String, ) : Call<ResponseBody>

    //消费流水
    @GET("berserker-search/search/personal/turnover")
    fun Cardget(@Header("synjones-auth") auth : String,
                @Query("current") page : Int
                ,@Query("size") size : String) : Call<ResponseBody>

    //查询一个月每天总消费
    @GET("berserker-search/statistics/turnover/sum/user?dateType=month&statisticsDateStr=day&type=2")
    fun getMonthYue(@Header("synjones-auth") auth : String,
                    @Query("dateStr") dateStr : String ) : Call<ResponseBody>

   //查询从哪天到哪天的消费
    @GET("berserker-search/statistics/turnover/count")
    fun searchDate(@Header("synjones-auth") auth : String,
                   @Query("timeFrom") timeForm : String,
                   @Query("timeTo") timeTo : String ) : Call<ResponseBody>

    //查询关键字
    @GET("berserker-search/search/personal/turnover")
    fun searchBills(@Header("synjones-auth") auth : String,
                   @Query("info") info : String,
                    @Query("current") page : Int
        ,@Query("size") size : String) : Call<ResponseBody>


    //修改限额
    @POST("berserker-app/ykt/tsm/modifyAcc")
    fun changeLimit(
        @Header("synjones-auth") auth : String,
        @Body json: JsonObject ) : Call<ResponseBody>

    //查询  feeitemid为281为网费，261为电费 223为宣城校区洗浴
    @FormUrlEncoded
    @POST("charge/feeitem/getThirdData")
    fun getFee(@Header("synjones-auth") auth : String,
               @Field("feeitemid") typeId : String,
               @Field("type") IEC : String = "IEC",
               @Field("level") level : String? = null,
               @Field("room") room : String? = null,
               @Field("telPhone") phoneNumber : String? = null) : Call<ResponseBody>
    //交费支付 3步走
    @FormUrlEncoded
    @POST("blade-pay/pay")
    fun pay(@Header("synjones-auth") auth : String,
            //同上 网费/电费
            @Field("feeitemid") typeId : Int?,
            //支付金额
            @Field("tranamt") pay : Float?,
            @Field("flag") flag : String?,
            @Field("paystep") paystep : Int,
            //寝室房间
            @Field("third_party") json : String?,
            @Field("paytypeid") paytypeid : Int?,
            @Field("paytype") paytype : String?,
            @Field("orderid") orderid : String?,
            //经过随机排序后的支付密码（身份证后六位）
            @Field("password") password : String?,
            @Field("uuid") cardId : String?,
            @Field("isWX") isWX : Int?
            ) : Call<ResponseBody>
    //挂失解挂
}
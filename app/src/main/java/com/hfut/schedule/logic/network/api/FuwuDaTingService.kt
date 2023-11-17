package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface FuwuDaTingService {
    //获取验证码图片
    //Cookie:
    // username=2023218529;
    // ASP.NET_SessionId=bulqinccmlvxaux40ptwl2p4;
    // pageToken=0771de34766722d2291e5ce2a2734813cfd6303b7bb84514f1b6382e893706ba
    @GET ("Login/GetValidateCode")
    fun getValiCode() : Call<ResponseBody>
//登录   sno=2023218529&pwd=MDEwMzcx&ValiCode=43476&remember=1&uclass=1s&zqcode=&json=true
    @FormUrlEncoded
    @POST ("Login/LoginBySnoQuery")
    fun Login(
              @Field("sno") username : String,
              @Field("pwd") Output : String,
              @Field("ValiCode") VailCode : String
    ) : Call<ResponseBody>
}
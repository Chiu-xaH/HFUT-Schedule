package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface OneService {
    //信息门户主页  XML
    @GET ("home/index")
    @Headers("User-Agent:MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
    fun getIndex() : Call<ResponseBody>


    //空教室  JSON
    //CampusID : {01}屯溪路校区  {02}翡翠湖  {03}宣城  {04}六安路
    @GET ("api/operation/emptyClass/build?campus_code-{CampusID}")
    @Headers("User-Agent:MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1",
        "Content-Type: application/json")
    fun searchEmptyRoom(@Path("CampusID") campusID : String) : Call<ResponseBody>
}
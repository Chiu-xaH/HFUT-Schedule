package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface OneService {
    //信息门户主页  XML
    @GET ("home/index")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getIndex() : Call<ResponseBody>


    //空教室  JSON
    //CampusID : {01}屯溪路校区  {02}翡翠湖  {03}宣城  {04}六安路
    @GET ("api/operation/emptyClass/build?campus_code-{CampusID}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17",
        "Content-Type: application/json")
    fun searchEmptyRoom(@Path("CampusID") campusID : String) : Call<ResponseBody>
}
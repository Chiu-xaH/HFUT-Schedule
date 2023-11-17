package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface OneService {

    @GET ("api/operation/library/getBorrowNum")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17",
        "Content-Type: application/json")
    fun getBorrowBooks(@Header("Authorization") Authorization : String) : Call<ResponseBody>

    @GET ("api/operation/library/getSubscribeNum")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17",
        "Content-Type: application/json")
    fun getSubBooks(@Header("Authorization") Authorization : String) : Call<ResponseBody>
    @GET ("api/operation/thirdPartyApi/schoolcard/balance")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17",
        "Content-Type: application/json")
    fun getCard(@Header("Authorization") Authorization : String) : Call<ResponseBody>

//&redirect=https%253A%2F%2Fone.hfut.edu.cn%2Fhome%2Findex%253Fcode%253DOC-1414541-a9LSJGgabm-wNkFjbIcAvtlUBHJyNTfN&code=OC-1414541-a9LSJGgabm-wNkFjbIcAvtlUBHJyNTfN
    @GET ("api/auth/oauth/getToken?type=portal")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getToken(@Query("redirect") redirect : String,
                  @Query("code") code : String
               ) : Call<ResponseBody>

    //空教室  JSON

    //api/operation/emptyClass/build?campus_code=03
    // {01}屯溪路校区  {02}翡翠湖  {03}宣城
    //获取JSON，各个楼建筑的building_code，再用building_code去查空教室
    @GET ("api/operation/emptyClass/build")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17",
        "Content-Type: application/json")
    fun selectBuilding(@Query("campus_code") campusID : String,
        @Header("Authorization") Authorization : String) : Call<ResponseBody>

    //api/operation/emptyClass/room?building_code=XC001&current=1

    @GET ("api/operation/emptyClass/room?current=1&size=30")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17",
        "Content-Type: application/json")
    fun searchEmptyRoom(@Query("building_code") building_code : String,
                        @Header("Authorization") Authorization : String) : Call<ResponseBody>
}
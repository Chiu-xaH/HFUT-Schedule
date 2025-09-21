package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface PeService {
    @POST("bdlp_h5_fitness_test/public/index.php/index/Index/checkLogin")
    fun checkLogin(
        @Header("Cookie") cookie : String
    ) : Call<ResponseBody>

    @POST("bdlp_h5_fitness_test/public/index.php/index/User/getStudentInfo")
    fun getUserInfo(
        @Header("Cookie") cookie : String
    ) : Call<ResponseBody>

}
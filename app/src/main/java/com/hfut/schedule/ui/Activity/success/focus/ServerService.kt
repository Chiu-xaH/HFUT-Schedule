package com.hfut.schedule.ui.Activity.success.focus

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ServerService {
    //向数据库查询数据
    @GET("get.php")
    fun getData(): Call<ResponseBody>
}
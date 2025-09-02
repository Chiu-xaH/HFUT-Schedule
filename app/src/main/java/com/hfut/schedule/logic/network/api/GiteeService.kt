package com.hfut.schedule.logic.network.api

import com.hfut.schedule.application.MyApplication
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Headers
import retrofit2.http.Path

interface GiteeService {
    //获取更新内容
    @GET("releases/tag/Android")
    fun getUpdate() : Call<ResponseBody>
    //下载安装包
    @HEAD("releases/download/Android/{filename}")
    @Headers("Referer: ${MyApplication.GITEE_UPDATE_URL}")
    fun download(@Path("filename")filename : String) : Call<Void>
}
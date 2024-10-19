package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GiteeService {
    //获取更新内容
    @GET("releases/tag/Android")
    fun getUpdate() : Call<ResponseBody>
    //下载安装包
    @GET("releases/download/Android/{version}.apk")
    fun download(@Path("version")version : String) : Call<ResponseBody>
}
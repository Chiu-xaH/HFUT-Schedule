package com.hfut.schedule.logic.network.api

import com.hfut.schedule.application.MyApplication
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GiteeService {
    //获取更新内容
    @GET("api/v5/repos/${MyApplication.GITHUB_DEVELOPER_NAME}/${MyApplication.GITHUB_REPO_NAME}/releases")
    fun getUpdate(
        @Query("access_token") token : String = MyApplication.GITEE_TOKEN,
        @Query("page") page : Int = 1,
        @Query("per_page") pageSize : Int = 100
    ) : Call<ResponseBody>
    //下载安装包
    @HEAD("${MyApplication.GITHUB_DEVELOPER_NAME}/${MyApplication.GITHUB_REPO_NAME}/releases/download/Android/{filename}")
    @Headers("Referer: ${MyApplication.GITEE_UPDATE_URL}")
    fun download(@Path("filename")filename : String) : Call<Void>
}
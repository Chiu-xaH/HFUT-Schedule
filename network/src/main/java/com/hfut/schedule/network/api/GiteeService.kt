package com.hfut.schedule.network.api

import com.hfut.schedule.network.util.Constant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GiteeService {
    //获取更新内容
    @GET("api/v5/repos/${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}/releases")
    fun getUpdate(
        @Query("access_token") token : String = Constant.GITEE_TOKEN,
        @Query("page") page : Int = 1,
        @Query("per_page") pageSize : Int = 100
    ) : Call<ResponseBody>
    //下载安装包
    @HEAD("${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}/releases/download/Android/{filename}")
    @Headers("Referer: ${Constant.GITEE_UPDATE_URL}")
    fun download(@Path("filename")filename : String) : Call<Void>
}
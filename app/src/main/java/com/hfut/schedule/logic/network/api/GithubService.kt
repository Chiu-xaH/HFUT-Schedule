package com.hfut.schedule.logic.network.api

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {
    // 获取仓库信息
    @GET("repos/{owner}/{repo}")
    fun getRepoInfo(
        @Path("owner") owner : String = MyApplication.GITHUB_DEVELOPER_NAME,
        @Path("repo") repo : String = MyApplication.GITHUB_REPO_NAME
    ) : Call<ResponseBody>
    // 获取开发者信息
    @GET("users/{username}/repos")
    fun getUserInfo(
        @Path("username") username : String = MyApplication.GITHUB_DEVELOPER_NAME,
    ) : Call<ResponseBody>
}
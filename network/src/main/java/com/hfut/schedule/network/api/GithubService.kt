package com.hfut.schedule.network.api

import com.hfut.schedule.network.util.Constant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    // 获取仓库信息
    @GET("repos/${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}")
    fun getRepoInfo() : Call<ResponseBody>
    // 获取开发者信息
    @GET("users/{username}/repos")
    fun getUserInfo(
        @Path("username") username : String = Constant.GITHUB_DEVELOPER_NAME,
    ) : Call<ResponseBody>

    // 获取仓库文件夹内容
    @GET("repos/${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}/contents/{path}")
    fun getFolderContent(
        @Path("path") path : String = "docs/update"
    ) : Call<ResponseBody>

    @GET("repos/${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}/issues?sort=created&state=all")
    fun getIssues(
        @Query("page") page : Int,
        @Query("per_page") pageSize : String = Constant.DEFAULT_PAGE_SIZE.toString()
    ) : Call<ResponseBody>

    @GET("repos/${Constant.GITHUB_DEVELOPER_NAME}/${Constant.GITHUB_REPO_NAME}/contributors")
    fun getContributors() : Call<ResponseBody>
}
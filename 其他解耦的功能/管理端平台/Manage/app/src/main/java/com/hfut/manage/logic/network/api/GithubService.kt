package com.hfut.manage.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface GithubService {
    @GET("/")
    fun getData() : Call<ResponseBody>
}
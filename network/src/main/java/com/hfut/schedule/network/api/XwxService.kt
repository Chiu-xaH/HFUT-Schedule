package com.hfut.schedule.network.api

import com.hfut.schedule.network.model.XwxDocPreviewRequest
import com.hfut.schedule.network.model.XwxFunctionsRequest
import com.hfut.schedule.network.model.XwxLoginRequest
import com.hfut.schedule.network.model.XwxSchoolListRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface XwxService {
    @POST("getSchoolList")
    @Headers("Content-Type: application/json")
    fun getSchoolList(
        @Body body : XwxSchoolListRequest = XwxSchoolListRequest()
    ) : Call<ResponseBody>

    @POST("api/login/user")
    @Headers("Content-Type: application/json")
    fun login(
        @Body body : XwxLoginRequest
    ) : Call<ResponseBody>

    @POST("api/office-service-ms/printType")
    @Headers("Content-Type: application/json")
    fun getFunctions(
        @Header("X-Authorization") token : String,
        @Body body : XwxFunctionsRequest
    ) : Call<ResponseBody>

    @POST("api/office-service-ms/eDoc/getPdfPic")
    @Headers("Content-Type: application/json")
    fun getDocPreview(
        @Header("X-Authorization") token : String,
        @Body body : XwxDocPreviewRequest
    ) : Call<ResponseBody>
}
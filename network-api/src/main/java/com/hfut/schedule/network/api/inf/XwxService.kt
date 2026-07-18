package com.hfut.schedule.network.api.inf

import com.hfut.schedule.network.api.model.request.xiaowuxing.XiaoWuXingDocPreviewRequest
import com.hfut.schedule.network.api.model.request.xiaowuxing.XiaoWuXingFunctionsRequest
import com.hfut.schedule.network.api.model.request.xiaowuxing.XiaoWuXingLoginRequest
import com.hfut.schedule.network.api.model.request.xiaowuxing.XiaoWuXingSchoolListRequest
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
        @Body body : XiaoWuXingSchoolListRequest = XiaoWuXingSchoolListRequest()
    ) : Call<ResponseBody>

    @POST("api/login/user")
    @Headers("Content-Type: application/json")
    fun login(
        @Body body : XiaoWuXingLoginRequest
    ) : Call<ResponseBody>

    @POST("api/office-service-ms/printType")
    @Headers("Content-Type: application/json")
    fun getFunctions(
        @Header("X-Authorization") token : String,
        @Body body : XiaoWuXingFunctionsRequest
    ) : Call<ResponseBody>

    @POST("api/office-service-ms/eDoc/getPdfPic")
    @Headers("Content-Type: application/json")
    fun getDocPreview(
        @Header("X-Authorization") token : String,
        @Body body : XiaoWuXingDocPreviewRequest
    ) : Call<ResponseBody>
}
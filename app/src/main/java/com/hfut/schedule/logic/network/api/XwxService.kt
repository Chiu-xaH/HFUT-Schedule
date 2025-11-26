package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.model.xwx.XwxDocPreviewRequestBody
import com.hfut.schedule.logic.model.xwx.XwxFunctionsRequestBody
import com.hfut.schedule.logic.model.xwx.XwxLoginRequestBody
import com.hfut.schedule.logic.model.xwx.XwxSchoolListRequestBody
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
        @Body body : XwxSchoolListRequestBody = XwxSchoolListRequestBody()
    ) : Call<ResponseBody>

    @POST("api/login/user")
    @Headers("Content-Type: application/json")
    fun login(
        @Body body : XwxLoginRequestBody
    ) : Call<ResponseBody>

    @POST("api/office-service-ms/printType")
    @Headers("Content-Type: application/json")
    fun getFunctions(
        @Header("X-Authorization") token : String,
        @Body body : XwxFunctionsRequestBody
    ) : Call<ResponseBody>

    @POST("api/office-service-ms/eDoc/getPdfPic")
    @Headers("Content-Type: application/json")
    fun getDocPreview(
        @Header("X-Authorization") token : String,
        @Body body : XwxDocPreviewRequestBody
    ) : Call<ResponseBody>
}








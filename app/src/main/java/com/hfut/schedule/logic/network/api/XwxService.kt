package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.model.xwx.XwxDocPreviewRequestBody
import com.hfut.schedule.logic.model.xwx.XwxFunctionsRequestBody
import com.hfut.schedule.logic.model.xwx.XwxLoginRequestBody
import com.hfut.schedule.logic.model.xwx.XwxSchoolListRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface XwxService {
    @POST("getSchoolList")
    fun getSchoolList(
        @Body body : XwxSchoolListRequestBody = XwxSchoolListRequestBody()
    ) : Call<ResponseBody>

    @POST("api/login/user")
    fun login(
        @Body body : XwxLoginRequestBody
    ) : Call<ResponseBody>

    @POST("api/office-service-ms/printType")
    fun getFunctions(
        @Body body : XwxFunctionsRequestBody
    ) : Call<ResponseBody>

    @POST("api/office-service-ms/eDoc/getPdfPic")
    fun getDocPreview(
        @Body body : XwxDocPreviewRequestBody
    ) : Call<ResponseBody>
}








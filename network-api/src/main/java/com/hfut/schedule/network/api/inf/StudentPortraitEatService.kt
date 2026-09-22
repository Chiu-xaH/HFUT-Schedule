package com.hfut.schedule.network.api.inf

import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.request.studentportrait.StudentPortraitEatRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface StudentPortraitEatService {
    @Headers("Content-Type: application/json")
    @POST("api/admin/dataway/xshx/eat/top3")
    fun getTop3(
        @Header("Authorization") authorization: String,
        @Header("Cookie") cookie: String,
        @Header("Referer") referer: String = Constant.STUDENT_PORTRAIT_URL,
        @Body body: StudentPortraitEatRequest
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("api/admin/dataway/xshx/eat/nianxiaofeiqingkuang")
    fun getAnnual(
        @Header("Authorization") authorization: String,
        @Header("Cookie") cookie: String,
        @Header("Referer") referer: String = Constant.STUDENT_PORTRAIT_URL,
        @Body body: StudentPortraitEatRequest
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("api/admin/dataway/xshx/eat/jydd")
    fun getFrequency(
        @Header("Authorization") authorization: String,
        @Header("Cookie") cookie: String,
        @Header("Referer") referer: String = Constant.STUDENT_PORTRAIT_URL,
        @Body body: StudentPortraitEatRequest
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("api/admin/dataway/xshx/eat/oneday")
    fun getOneDay(
        @Header("Authorization") authorization: String,
        @Header("Cookie") cookie: String,
        @Header("Referer") referer: String = Constant.STUDENT_PORTRAIT_URL,
        @Body body: StudentPortraitEatRequest
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("api/admin/dataway/xshx/eat/average")
    fun getAverage(
        @Header("Authorization") authorization: String,
        @Header("Cookie") cookie: String,
        @Header("Referer") referer: String = Constant.STUDENT_PORTRAIT_URL,
        @Body body: StudentPortraitEatRequest
    ): Call<ResponseBody>
}

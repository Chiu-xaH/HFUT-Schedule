package com.hfut.schedule.logic.network.api

import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

// 学工系统 今日校园
interface StuService {
    // 刷新cookie 或登录用
    @GET("xsfw/sys/jbxxapp/*default/index.do")
    fun login(@Query("ticket") ticket : String?, ) : Call<ResponseBody>
    @POST("xsfw/sys/xgutilapp/userinfo/getConfigUserInfo.do")
    fun checkLogin(@Header("Cookie") cookie : String) : Call<ResponseBody>
    // 获取学工服务
    @FormUrlEncoded
    @POST("xsfw/sys/xggzptapp/modules/xszm/getFxYy.do")
    fun getStuApps(
        @Header("Cookie") cookie : String,
        @Field("data") data: String = """
            {"FZDM":"SCYY","pageSize":"500","pageNum":1}
        """.trimIndent()
    ) : Call<ResponseBody>
}

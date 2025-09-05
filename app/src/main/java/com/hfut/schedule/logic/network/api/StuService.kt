package com.hfut.schedule.logic.network.api

import retrofit2.Call
import okhttp3.ResponseBody
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
    // 获取综测
    // Urlencoded data={"CPXN":"2023","CPXQ":"3"}
    @FormUrlEncoded
    @POST("xsfw/sys/swmzhcptybbapp/StudentApplyController/getStudentZhcpDetail.do")
    fun getGrades(@Header("Cookie") cookie : String) : Call<ResponseBody>
    // 学生信息
    // Urlencoded data={}
    @FormUrlEncoded
    @POST("xsfw/sys/swmjbxxapp/StudentBasicInfo/queryStudentBasicInfo.do")
    fun getStudentInfo(@Header("Cookie") cookie : String,@FieldMap data: Map<String, String> = mapOf()) : Call<ResponseBody>
}

/*
https://stu.hfut.edu.cn/xsfw/sys/swmzhcptybbapp/*default/index.do?wxType=1
 */
https://cas.hfut.edu.cn/cas/login?service=https%3A%2F%2Fstu.hfut.edu.cn%2Fxsfw%2Fsys%2Fswmzhcptybbapp%2F*default%2Findex.do%3FwxType%3D1
伴随三大件Cookie JSESSIONID SESSION LOGIN_FLAVORING
val ONE = prefs.getString("ONE", "")
val TGC = prefs.getString("TGC", "")
val cookies = "$ONE;$TGC"

响应302后 取Location https://stu.hfut.edu.cn/xsfw/sys/swmzhcptybbapp/*default/index.do?wxType=1&ticket=ST-176961-So5PMRbkU0O3MB0xHAgqy4pne4Exxzx-zhxy-ngx-006

跟随Location 响应头拿Set-Cookie，保存里面的_WEU键值即可

这次写运用DataStore

 */
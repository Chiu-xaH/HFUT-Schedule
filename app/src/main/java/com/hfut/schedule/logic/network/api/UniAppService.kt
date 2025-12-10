package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.model.uniapp.UniAppEmptyClassroomRequest
import com.hfut.schedule.logic.model.uniapp.UniAppSearchProgramRequest
import com.hfut.schedule.logic.util.network.getPageSize
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// 合工大教务
interface UniAppService {
    // 登录
    @POST("token/password/passwordLogin")
    fun login(
        @Query("username") studentId : String,
        @Query("password") password : String,
        @Query("appId") appId : String = "APP_ID",
        @Query("deviceId") deviceId : String = "DEVICE_ID"
    ) : Call<ResponseBody>
    // 同班同学
    @GET("eams-micro-server/api/v1/lesson/student/class-mates/{id}")
    fun getClassmates(
        @Path("id") lessonId : String,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 成绩
    @GET("eams-micro-server/api/v1/grade/student/grades")
    fun getGrades(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 考试
    @GET("eams-micro-server/api/v1/exam/student/exam")
    fun getExams(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 课表
    @GET("eams-micro-server/api/v1/lesson/student/course-table/{semseter}")
    fun getCourses(
        @Path("semseter") semseter : Int,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 全校培养方案
    @POST("eams-micro-server/api/v1/plan/search/major")
    fun searchPrograms(
        @Body body : UniAppSearchProgramRequest,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 全校培养方案详情
    @GET("eams-micro-server/api/v1/plan/search/major/plan-courses/{id}")
    fun getProgramById(
        @Path("id") id : Int,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 校区
    @GET("eams-micro-server/api/v1/room/place/campus")
    fun getCampus(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 建筑楼
    @GET("eams-micro-server/api/v1/room/place/building")
    fun getBuildings(
        @Header("Authorization") auth : String,
        @Query("campusAssoc") campusAssoc : String = "",
    ) : Call<ResponseBody>
    // 空教室
    @POST("eams-micro-server/api/v1/room/place/rooms")
    fun getEmptyClassrooms(
        @Body body : UniAppEmptyClassroomRequest,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 教室课表
    @GET("eams-micro-server/api/v1/lesson/room/searchRooms")
    fun searchClassrooms(
        @Query("name") name : String,
        @Query("queryPage__") queryPage : String = "1,${getPageSize()}",
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    @GET("eams-micro-server/api/v1/lesson/room/getLessons")
    fun getClassroomLessons(
        @Query("semesterAssoc") semester : Int,
        @Query("roomAssoc") roomId : Int,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 评教 非必需，教务系统就有
    // TODO
    // 个人信息 非必需，教务系统就有
    // TODO
    // 培养方案 非必需，教务系统就有
    // TODO
    // 转专业 非必需，教务系统就有
    // TODO
}

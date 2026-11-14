package com.hfut.schedule.network.api.inf

import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.request.uniapp.UniAppEmptyClassroomRequest
import com.hfut.schedule.network.api.model.request.uniapp.UniAppSearchProgramRequest
import com.xah.common.logic.util.EMPTY_STRING
import okhttp3.ResponseBody
import retrofit2.Call
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
        @Query("campusAssoc") campusAssoc : String = EMPTY_STRING,
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
        @Query("queryPage__") queryPage : String = "1,${Constant.DEFAULT_PAGE_SIZE}",
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    @GET("eams-micro-server/api/v1/lesson/room/getLessons")
    fun getClassroomLessons(
        @Query("semesterAssoc") semester : Int,
        @Query("roomAssoc") roomId : Int,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 评教
    @GET("eams-app-service/api/v1/for-std/summative-eva/task/semester/{semesterId}")
    fun getSurveyList(
        @Path("semesterId") semesterId : Int,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 个人信息
    @GET("eams-micro-server/api/v1/student/base-info")
    fun getPersonInfo(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 培养方案
    @GET("eams-micro-server/api/v1/plan/student/my-program")
    fun getProgramList(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 培养方案
    @GET("eams-micro-server/api/v1/plan/student/plan-courses/{programId}/0")
    fun getProgram(
        @Path("programId") programId : Int,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 培养方案完成情况
    @GET("eams-micro-server/api/v1/plan/search/completion/cultivateTypes")
    fun getProgramCompletionList(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 培养方案完成情况
    @GET("eams-micro-server/api/v1/plan/search/completion/info")
    fun getProgramCompletion(
        @Query("code") code : String,
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 转专业
    @GET("eams-micro-server/api/v1/change/student/change-batchs")
    fun getTransferList(
        @Header("Authorization") auth : String
    ) : Call<ResponseBody>
    // 开课查询
    @GET("eams-micro-server/api/v1/lesson-search")
    fun searchCourses(
        @Query("term") keyword : String,// 课程名 教学班名 教师名
        @Header("Authorization") auth : String,
        @Query("queryPage__") page : String = "1,${Constant.DEFAULT_PAGE_SIZE}",
    ) : Call<ResponseBody>
}
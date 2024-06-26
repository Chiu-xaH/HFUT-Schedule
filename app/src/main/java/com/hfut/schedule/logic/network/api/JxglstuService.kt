package com.hfut.schedule.logic.network.api

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface JxglstuService {
    //带着Cookie登录教务系统主页
    @GET("neusoft-sso/login")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun jxglstulogin(@Header("Cookie") Cookie : String) : Call<ResponseBody>

    //获取学生Id
    @GET("for-std/course-table")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getStudentId(@Header("Cookie") Cookie : String) : Call<ResponseBody>

    //获取需要POST的lessonsId数组
    //eams5-student/for-std/course-table/get-data?bizTypeId=23&semesterId=234&dataId=170317
    @GET("for-std/course-table/get-data")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getLessonIds(@Header("Cookie") Cookie : String,
                     @Query("bizTypeId") bizTypeId : String,
                     @Query("semesterId") semesterId : String,
                     @Query("dataId") dataId : String
                     ) : Call<ResponseBody>

    //获取课程表JSON,需要提交前面获取到的数据才可以，否则返回500错误
    //课程表  JSON
    @POST("ws/schedule-table/datum")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17",
        "Content-Type: application/json")
    fun getDatum(@Header("Cookie") Cookie : String,
                 @Body json: JsonObject
    ) : Call<ResponseBody>

    //学生信息  XML
    @GET("for-std/student-info/info/{studentId}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getInfo(@Header("Cookie") Cookie : String,
                @Path("studentId") studentId : String) : Call<ResponseBody>
    //培养方案 JSON
    @GET("for-std/program/root-module-json/{studentId}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getProgram(@Header("Cookie") Cookie : String,
                @Path("studentId") studentId : String) : Call<ResponseBody>

    //成绩单  XML
    @GET("for-std/grade/sheet/info/{studentId}?semester=")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getGrade(@Header("Cookie") Cookie : String,
                 @Path("studentId") studentId : String) : Call<ResponseBody>
    //考试查询   XML
    @GET("for-std/exam-arrange/info/{studentId}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getExam(@Header("Cookie") Cookie : String,
                @Path("studentId") studentId : String) : Call<ResponseBody>

    //开课查询   JSON
    @GET("for-std/lesson-search/semester/{semester}/search/{studentId}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun searchCourse(@Header("Cookie") Cookie : String,
                     @Path("studentId") studentId : String,
                     @Path("semester") semester : Int,
                     @Query("nameZhLike") className : String?,
                     @Query("queryPage__") queryPage : String,
                     @Query("courseNameZhLike") courseName : String?
                     ) : Call<ResponseBody>

    //获取教评列表
    @GET("for-std/lesson-survey/{semester}/search/{studentId}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getSurveyList(@Header("Cookie") Cookie : String,
                      @Path("studentId") studentId : String,
                      @Path("semester") semester : Int,) : Call<ResponseBody>

    //获取具体教评内容
    @GET("for-std/lesson-survey/start-survey/{teacherId}/get-data")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getSurveyInfo(@Header("Cookie") Cookie : String,
                      @Path("teacherId") teacherId : String) : Call<ResponseBody>

    //获取具体教评内容
    @GET("for-std/lesson-survey/start-survey/{teacherId}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun getSurveyToken(@Header("Cookie") Cookie : String,
                      @Path("teacherId") teacherId : String,
                       @Query("REDIRECT_URL") REDIRECT_URL : String) : Call<ResponseBody>

    //提交教评
    @POST("for-std/lesson-survey/submit-survey")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36 Edg/118.0.2088.17")
    fun postSurvey(@Header("Cookie") Cookie : String,
                   @Body json: JsonObject) : Call<ResponseBody>

}



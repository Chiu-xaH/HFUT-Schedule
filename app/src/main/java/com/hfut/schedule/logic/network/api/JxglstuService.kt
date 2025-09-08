package com.hfut.schedule.logic.network.api

import com.google.gson.JsonObject
import com.hfut.schedule.application.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface JxglstuService {
    @GET("/eams5-student")
    @Headers(MyApplication.PC_UA)
    fun checkCanUse() : Call<ResponseBody>

    //带着Cookie登录教务系统主页
    @GET("neusoft-sso/login")
    @Headers(MyApplication.PC_UA)
    fun jxglstulogin(@Header("Cookie") Cookie : String) : Call<ResponseBody>

    //获取学生Id
    @GET("for-std/course-table")
    @Headers(MyApplication.PC_UA)
    fun getStudentId(@Header("Cookie") Cookie : String) : Call<ResponseBody>

    //获取需要POST的lessonsId数组
    //eams5-student/for-std/course-table/get-data?bizTypeId=23&semesterId=234&dataId=170317
    @GET("for-std/course-table/get-data")
    @Headers(MyApplication.PC_UA)
    fun getLessonIds(@Header("Cookie") Cookie : String,
                     @Query("bizTypeId") bizTypeId : String,
                     @Query("semesterId") semesterId : String,
                     @Query("dataId") dataId : String
                     ) : Call<ResponseBody>

    //获取课程表JSON,需要提交前面获取到的数据才可以，否则返回500错误
    //课程表  JSON
    @POST("ws/schedule-table/datum")
    @Headers(MyApplication.PC_UA,
        "Content-Type: application/json")
    fun getDatum(@Header("Cookie") Cookie : String,
                 @Body json: JsonObject
    ) : Call<ResponseBody>

    //学生信息  XML
    @GET("for-std/student-info/info/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun getInfo(@Header("Cookie") Cookie : String,
                @Path("studentId") studentId : String) : Call<ResponseBody>
    //培养方案 JSON
    @GET("for-std/program/root-module-json/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun getProgram(@Header("Cookie") Cookie : String,
                @Path("studentId") studentId : String) : Call<ResponseBody>

    //成绩单  XML
    @GET("for-std/grade/sheet/info/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun getGrade(@Header("Cookie") Cookie : String,
                 @Path("studentId") studentId : String,
                 @Query("semester") semester : Int?,) : Call<ResponseBody>
    //考试查询   XML
    @GET("for-std/exam-arrange/info/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun getExam(@Header("Cookie") Cookie : String,
                @Path("studentId") studentId : String) : Call<ResponseBody>

    //开课查询   JSON
    @GET("for-std/lesson-search/semester/{semester}/search/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun searchCourse(@Header("Cookie") Cookie : String,
                     @Path("studentId") studentId : String,
                     @Path("semester") semester : Int,
                     @Query("nameZhLike") className : String?,
                     @Query("queryPage__") queryPage : String,
                     @Query("courseNameZhLike") courseName : String?,
                     @Query("codeLike") courseId : String?
                     ) : Call<ResponseBody>

    //获取教评列表
    @GET("for-std/lesson-survey/{semester}/search/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun getSurveyList(@Header("Cookie") Cookie : String,
                      @Path("studentId") studentId : String,
                      @Path("semester") semester : Int,) : Call<ResponseBody>

    //获取具体教评内容
    @GET("for-std/lesson-survey/start-survey/{teacherId}/get-data")
    @Headers(MyApplication.PC_UA)
    fun getSurveyInfo(@Header("Cookie") Cookie : String,
                      @Path("teacherId") teacherId : String) : Call<ResponseBody>

    //获取具体教评内容
    @GET("for-std/lesson-survey/start-survey/{teacherId}")
    @Headers(MyApplication.PC_UA)
    fun getSurveyToken(@Header("Cookie") Cookie : String,
                      @Path("teacherId") teacherId : String,
                       @Query("REDIRECT_URL") REDIRECT_URL : String) : Call<ResponseBody>

    //提交教评
    @POST("for-std/lesson-survey/submit-survey")
    @Headers(MyApplication.PC_UA)
    fun postSurvey(@Header("Cookie") Cookie : String,
                   @Body json: JsonObject) : Call<ResponseBody>

    //获取学籍照片
    @GET("students/avatar/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun getPhoto(@Header("Cookie") Cookie : String,  @Path("studentId") studentId : String,) : Call<ResponseBody>


    //先验证,否则无法爬选课信息
    @GET("for-std/course-select")
    @Headers(MyApplication.PC_UA)
    fun verify(@Header("Cookie") Cookie : String,) : Call<ResponseBody>

    //获取选课
    @FormUrlEncoded
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0","Content-Type: application/x-www-form-urlencoded")
    @POST("ws/for-std/course-select/open-turns")
    fun getSelectCourse(
        @Field("bizTypeId") bizTypeId: Int,
        @Field("studentId") studentId: String,
        @Header("Cookie") cookie: String
    ): Call<ResponseBody>

    //获取具体的选课
    @FormUrlEncoded
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0","Content-Type: application/x-www-form-urlencoded")
    @POST("ws/for-std/course-select/addable-lessons")
    fun getSelectCourseInfo (
        @Field("turnId") turnId: Int,
        @Header("Cookie") cookie: String
    ): Call<ResponseBody>

    //选课人数
    @FormUrlEncoded
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0","Content-Type: application/x-www-form-urlencoded")
    @POST("ws/for-std/course-select/std-count")
    fun getCount (
        @Field("lessonIds[]") id: Int,
        @Header("Cookie") cookie: String
    ): Call<ResponseBody>

    //准备选/退课请求，add选课drop退课
    @FormUrlEncoded
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0","Content-Type: application/x-www-form-urlencoded")
    @POST("ws/for-std/course-select/{type}-request")
    fun getRequestID (
        @Field("studentAssoc") studentId: String,
        @Field("lessonAssoc") lessonId: String,
        @Field("courseSelectTurnAssoc") courseId: String,
        @Header("Cookie") cookie: String,
        @Path("type") type : String
    ): Call<ResponseBody>

    //选/退课
    @FormUrlEncoded
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0","Content-Type: application/x-www-form-urlencoded")
    @POST("ws/for-std/course-select/add-drop-response")
    fun postSelect (
        @Field("studentId") studentId: String,
        @Field("requestId") requestId: String,
        @Header("Cookie") cookie: String
    ): Call<ResponseBody>

    //获取已经选的课
    @FormUrlEncoded
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0","Content-Type: application/x-www-form-urlencoded")
    @POST("ws/for-std/course-select/selected-lessons")
    fun getSelectedCourse (
        @Field("studentId") studentId: String,
        @Field("turnId") courseId : String,
        @Header("Cookie") cookie: String
    ): Call<ResponseBody>

    //我的档案（个人信息的补充）
    @GET("my/profile")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    fun getMyProfile( @Header("Cookie") cookie: String) : Call<ResponseBody>

    //转专业申请列表
    @GET("for-std/change-major-apply/index/{studentId}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    fun getTransferList(
        @Header("Cookie") cookie: String,
        @Path("studentId") studentId : Int
    ): Call<ResponseBody>

    //具体转专业申请列表
    //batchId
    @GET("for-std/change-major-apply/get-applies")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    fun getTransfer(
        @Header("Cookie") cookie: String,
        @Query("batchId") batchId : String,
        @Query("studentId") studentId : Int,
        @Query("auto") auto : Boolean = false
    ): Call<ResponseBody>

    //我的转专业申请
    @GET("for-std/change-major-apply/get-my-applies")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    fun getMyTransfer(
        @Header("Cookie") cookie: String,
        @Query("batchId") batchId : String,
        @Query("studentId") studentId : Int
    ): Call<ResponseBody>

    //撤销转专业申请
    @FormUrlEncoded
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    @POST("for-std/change-major-apply/cancel")
    fun cancelTransfer(
        @Header("Cookie") cookie: String,
        @Field("batchId") batchId : String,
        @Field("studentId") studentId : String,
        @Field("applyId") applyId : String,
        ///for-std/change-major-apply/my-applies?PARENT_URL=/for-std/change-major-apply/index/{studentID}&batchId={}&studentId={}
        @Field("REDIRECT_URL") redirectUrl: String ,
    ): Call<ResponseBody>

    //提交转专业申请需要一个_T_std_change_major_apply_new_form的Cookie
    //获_T_std_change_major_apply_new_form
    @GET("for-std/change-major-apply/new")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    fun getFormCookie(
        @Header("Cookie") cookie: String,
        @Query("REDIRECT_URL") redirectUrl: String ,
        @Query("submitId") id : String,
        @Query("batchId") batchId: String,
        @Query("studentId") studentId : String
    ): Call<ResponseBody>
    //提交转专业申请
    /*
    *       <option value="1">个人原因-创业</option>
            <option value="2">个人原因-工作实践</option>
            <option value="3">个人原因-出国出境</option>
            <option value="4">个人原因-厌学</option>
            <option value="5">个人原因-不适应课程学习</option>
            <option value="6">个人原因-不适应校园生活</option>
            <option value="7">个人原因-结婚生子</option>
            <option value="8">个人原因-精神疾病</option>
            <option value="9">个人原因-传染疾病</option>
            <option value="10">个人原因-其他疾病</option>
            <option value="11">个人原因-心理疾病</option>
            <option value="12">家庭原因-经济困难</option>
            <option value="13">家庭原因-照顾家人</option>
            <option value="14">其他</option>
            <option value="15">个人原因-休学期满未按时复学</option>
            <option value="16">个人原因-长期不参加教学活动</option>
            <option value="17">个人原因-超过最长学习年限</option>
            <option value="18">个人原因-成绩低劣</option>
            <option value="1246">复读</option>
            <option value="1226">短缺学分</option>
            <option value="1286">转专业-考核</option>
            <option value="41">身体康复</option>
            <option value="42">留学期满</option>
            <option value="43">创业、实习结束</option>
            <option value="44">个人原因-退伍</option>
            <option value="45">个人原因-入伍</option>
            <option value="1208">短缺学分</option>
            <option value="1209">不喜欢本专业</option>
            <option value="1210">转专业</option>
            <option value="1211">延长学制</option>
            <option value="1266">学习困难</option>
    * */
    @Multipart
    @POST("for-std/change-major-apply/save")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    fun postTransfer(
        @Header("Cookie") cookie: String,
        // 固定字符串,参考上面
        @Part("stdAlterReasonAssoc") reasonId: RequestBody = "1286".toRequestBody("text/plain".toMediaTypeOrNull()),
        @Part("applyRemark") remark: RequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull()),
        @Part file: MultipartBody.Part? = null, // 可选文件
        // 必须项,从我的档案接口获取手机号,或者让用户自己输
        @Part("telephone") telephone: RequestBody,
        @Part("email") email: RequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull()),
        // 固定字符串 REDIRECT_URL = /for-std/change-major-apply/apply?PARENT_URL=/for-std/change-major-apply/index/{studentId}&batchId={batchId}&studentId={studentId}
        @Part("REDIRECT_URL") redirectUrl : RequestBody,
        @Part("changeMajorBatchAssoc") batchId : RequestBody,
        @Part("studentAssoc") studentID :RequestBody,
        @Part("changeMajorSubmitAssoc") id: RequestBody // 转专业的专业列表里的id
    ): Call<ResponseBody>

    //我的转专业详情
    @GET("for-std/change-major-apply/info/{listId}")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    fun getMyTransferInfo(
        @Header("Cookie") cookie: String,
        @Path("listId") listId : Int,
        @Query("studentId") studentId : Int,
    ): Call<ResponseBody>


    //获取培养方案完成情况
    @GET("ws/student/home-page/programCompletionPreview")
    @Headers("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 Edg/122.0.0.0")
    fun getProgramCompletion(
        @Header("Cookie") cookie: String
    ): Call<ResponseBody>

    //获取培养方案完成情况 2.0
    @GET("for-std/program-completion-preview/json/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun getProgramPerformance(@Header("Cookie") Cookie : String,
                   @Path("studentId") studentId : Int) : Call<ResponseBody>

    // 获取BizTypeID
    @GET("for-std/course-table/info/{studentId}")
    @Headers(MyApplication.PC_UA)
    fun getBizTypeId(@Header("Cookie") Cookie : String,
                     @Path("studentId") studentId : Int) : Call<ResponseBody>

    // 获取作息 不同校区上课时间不同
    data class LessonTimeRequest(val timeTableLayoutId : Int)
    @POST("ws/schedule-table/timetable-layout")
    @Headers(MyApplication.PC_UA)
    fun getLessonTimes(@Header("Cookie") cookie : String, @Body studentId : LessonTimeRequest) : Call<ResponseBody>
    // 教材
    @GET("for-std/lesson-textbook/get-data")
    @Headers(MyApplication.PC_UA)
    fun getCourseBook(
        @Header("Cookie") cookie : String,
        @Query("bizTypeId") bizTypeId : Int,
        @Query("semesterId") semesterId : Int,
        @Query("dataId") studentId : Int
    ) : Call<ResponseBody>
}



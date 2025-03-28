package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CommunityService {
    @GET("api/sys/cas/client/validateLogin?service=https:%2F%2Fcommunity.hfut.edu.cn%2F")
    fun Login(@Query("ticket") ticket : String) : Call<ResponseBody>
    //平均成绩
    @GET("api/business/score/querytotalscore")
    fun getAvgGrade(@Header("X-Access-Token") token : String) : Call<ResponseBody>
    //成绩
    @GET("api/business/score/scoreselect")
    fun getGrade(@Header("X-Access-Token") token : String,
                 @Query("xn") year : String,
                 @Query("xq") term : String) : Call<ResponseBody>
    @GET("api/business/score/querymyprogress")
    fun getAllAvgGrade(@Header("X-Access-Token") Token : String) : Call<ResponseBody>
    //挂科率
    @GET("api/business/coursefailrate/list")
    fun getFailRate(
        @Header("X-Access-Token") token : String,
        @Query("courseName") courseName : String,
        @Query("pageNo") page : String
        , @Query("pageSize") size : String
    ) : Call<ResponseBody>
    //图书检索
    @GET("api/business/book/search")
    fun searchBooks(
        @Header("X-Access-Token") token : String,
        @Query("name") name: String,
        @Query("pageNo") page : String
        , @Query("pageSize") size : String
    ) : Call<ResponseBody>
    //考试（接口由于学校不发消息，废弃了）
    @GET("api/business/examarrangement/listselect")
    fun getExam(@Header("X-Access-Token") token : String) : Call<ResponseBody>
    //正借书籍
    @GET("api/business/book/lending/lendingList")
    fun getBorrowedBook(
        @Header("X-Access-Token") token : String,
        @Query("pageNo") page : String,
        @Query("pageSize") size : String) : Call<ResponseBody>
    //借阅历史
    @GET("api/business/book/lending/historyList?name=")
    fun getHistoryBook(
        @Header("X-Access-Token") token : String,
        @Query("pageNo") page : String,
        @Query("pageSize") size : String) : Call<ResponseBody>
    //逾期书籍
    @GET("api/business/book/lending/overdueList")
    fun getOverDueBook(
        @Header("X-Access-Token") token : String,
        @Query("pageNo") page : String,
        @Query("pageSize") size : String) : Call<ResponseBody>
    @GET("api/business/book/detail")
    fun getBookPosition(
        @Header("X-Access-Token") token : String,
        @Query("callNo") callNo : String
    ) : Call<ResponseBody>
    //课表 //根据选择的好友学号查看课表
    @GET("api/business/coursearrangement/listselect")
    fun getCourse(@Header("X-Access-Token") token : String, @Query("username") studentID : String?) : Call<ResponseBody>
    //今天
    @GET("api/mobile/community/homePage/today")
    fun getToday(@Header("X-Access-Token") token : String) : Call<ResponseBody>
    //查看好友
    @GET("api/business/coursefriendapply/list")
    fun getFriends(@Header("X-Access-Token") token : String) : Call<ResponseBody>
    //查看有谁申请想查看我的课表
    @GET("api/business/coursefriendapply/apgelist")
    fun getApplyingList(@Header("X-Access-Token") token : String, @Query("pageSize") pageSize : String, @Query("applyUsername")applyUsername : String = "", @Query("pageNo") page : Int = 1) : Call<ResponseBody>
    //申请添加好友
    data class RequestJsonApply(val applyUserId: String)
    @POST("api/business/coursefriendapply/add") //传入学号
    fun applyAdd(@Header("X-Access-Token") token : String, @Body requestJson: RequestJsonApply) : Call<ResponseBody>
    //打开/关闭好友课表
    data class RequestJson(val friendEnabled: Int = 1) //0代表请求关闭开关 1代表请求开启开关
    @POST("api/business/sys/user/usingfriendcourse")
    fun switchShare(@Header("X-Access-Token") token : String, @Body requestJson: RequestJson) : Call<ResponseBody>
    //同意申请
    data class RequestApplyingJson(val id: String,val status : Int = 1) //0代表拒绝 1代表同意
    @POST("api/business/coursefriendapply/edit")
    fun checkApplying(@Header("X-Access-Token") token : String, @Body requestJson: RequestApplyingJson) : Call<ResponseBody>
}
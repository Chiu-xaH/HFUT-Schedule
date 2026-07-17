package com.hfut.schedule.network.model.response.community

import com.google.gson.annotations.SerializedName

data class CommunityTodayResponse(
    val result : CommunityToday
)

data class CommunityToday(
    val todayCourse : CommunityTodayCourse,
    val bookLending : CommunityTodayBookBorrow,
    val todayExam : CommunityTodayExam,
    val todayActivity : CommunityTodayActivity
)

data class CommunityTodayCourse(
    val startTime : String?,
    val endTime : String?,
    val place : String?,
    val courseName : String?,
    val className : String?
)

data class CommunityTodayBookBorrow(
    val bookName : String?,
    val outTime : String?,
    val dueTime : String?,
    val returnTime : String?
)

data class CommunityTodayActivity(
    val activitySubject : String?,
    val activityName : String?,
    val startTime : String?,
    val endTime : String?,
    val qrCodeUrl : String?,
    @SerializedName("activitySubject_dictText")
    val activitySubjectDesc : String?
)

data class CommunityTodayExam(
    val courseName : String?,
    val place : String?,
    val startTime: String?,
    val endTime: String?
)
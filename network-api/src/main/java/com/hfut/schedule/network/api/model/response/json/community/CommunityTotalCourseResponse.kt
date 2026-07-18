package com.hfut.schedule.network.api.model.response.json.community

import com.google.gson.annotations.SerializedName

data class CommunityTotalCourseResponse(
    val result : CommunityTotalCourse
)

data class CommunityTotalCourse(
    @SerializedName("courseBasicInfoDTOList")
    val basicInfoList : List<CommunityCourseBasicInfo>,
    val startTime : List<String>,
    val endTime : List<String>,
    @SerializedName("xn")
    val termYear : String,
    @SerializedName("xq")
    val termPeriod : String,
    val start : String,
    val end : String,
    val totalWeekCount : Int,
    val currentWeek : Int
)

data class CommunityCourseBasicInfo(
    val courseName : String,
    val credit : Double,
    val className : String,
    val courseId : String,
    @SerializedName("trainingCategoryName_dictText")
    val type : String?,
    @SerializedName("courseDetailDTOList")
    val detailList : List<CommunityCourseDetail>
)

data class CommunityCourseDetail(
    val section : Int,
    val sectionCount : Int,
    val place : String?,
    val teacher : String,
    val classTime : String,
    val weekCount : List<Int>,
    val week : Int,
    val name : String,
    @SerializedName("campus_dictText")
    val campus : String
)
package com.hfut.schedule.network.model.response.community

import com.google.gson.annotations.SerializedName

data class CommunityFailRateResponse(
    val result : CommunityFailRate
)

data class CommunityFailRate(
    val records : List<CommunityFailRateRecord>,
    val current : Int,
    val pages  : Int
)

data class CommunityFailRateRecord(
    val courseName : String,
    @SerializedName("courseMetaId")
    val courseCode : String,
    @SerializedName("courseFailRateDTOList")
    val courseFailRateList : List<CommunityCourseFailRate>
)

data class CommunityCourseFailRate(
    @SerializedName("xn")
    val termYear : String,//学期
    @SerializedName("xq")
    val termPeriod : String,//第几学期
    val avgScore : Double,//平均分
    val totalCount : Int,//总人数
    val failCount : Int,//挂科人数
    val successRate : Float//挂科率
)

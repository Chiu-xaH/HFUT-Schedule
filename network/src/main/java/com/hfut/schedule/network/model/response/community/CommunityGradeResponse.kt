package com.hfut.schedule.network.model.response.community

import com.google.gson.annotations.SerializedName

data class CommunityGradeResponse(
    val result : CommunityGrade
)

data class CommunityGrade(
    val gpa : Double,
    val classRanking : String,
    val majorRanking : String,
    @SerializedName("scoreInfoDTOList")
    val scoreInfoList : List<CommunityScoreInfo>
)

data class CommunityScoreInfo(
    val courseName : String,
    val score : Double,
    val credit : Double,
    val gpa : Float,
    @SerializedName("pass")
    val passed : Boolean
)

data class CommunityGradeAverageResponse(
    val result : CommunityGradeAverage
)

data class CommunityGradeAverage(
    val myAvgScore : Double?,
    val myAvgGpa : Double?,
    val majorAvgScore : Double?,
    val majorAvgGpa : Double?,
    val majorAvgScoreRanking : String?,
    val majorAvgGpaRanking : String?
)

data class CommunityGradeAllResponse(
    val result : List<CommunityGradeAll>
)

data class CommunityGradeAll(
    val myAvgScore : Double?,
    val myAvgGpa : Double?,
    val majorAvgScore : Double?,
    val majorAvgGpa : Double?,
    val maxAvgScore : Double?,
    val maxAvgGpa : Double?
)


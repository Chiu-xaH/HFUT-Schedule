package com.hfut.schedule.network.api.model.response.json.oneform

import com.google.gson.annotations.SerializedName

data class OneFormStudentAchievementResponse(
    val code: Int?,
    val msg: String?,
    val data: OneFormStudentAchievementData?
)

data class OneFormStudentAchievementData(
    @SerializedName("jdpmList")
    val gpaRankings: List<OneFormGpaRanking>?,
    @SerializedName("kccjList")
    val courseGrades: List<OneFormCourseGrade>?
)

data class OneFormGpaRanking(
    @SerializedName("SORT")
    val sort: Int?,
    @SerializedName("XN")
    val academicYear: String?,
    @SerializedName("ZJD")
    val totalGpa: String?,
    @SerializedName("ZYPM")
    val majorRank: String?
)

data class OneFormCourseGrade(
    @SerializedName("KCMC")
    val courseName: String?,
    @SerializedName("XN")
    val academicYear: String?,
    @SerializedName("XQ")
    val semester: String?,
    @SerializedName("KCCJ")
    val score: String?,
    @SerializedName("JSGZ")
    val gradingRule: String?,
    @SerializedName("KCLX")
    val courseType: String?,
    @SerializedName("XF")
    val credit: String?,
    @SerializedName("ZYPM")
    val majorRank: String?
)

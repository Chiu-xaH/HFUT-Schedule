package com.hfut.schedule.network.api.model.response.json.oneform

import com.google.gson.annotations.SerializedName

data class OneFormStudentAchievementResponse(
    val code: Int?,
    val msg: String?,
    val data: OneFormStudentAchievementData?
)

data class OneFormStudentAchievementData(
    @SerializedName("jdpmList")
    val gpaRankings: List<OneFormGpaRanking>? = null,
    @SerializedName("kccjList")
    val courseGrades: List<OneFormCourseGrade>? = null,
    @SerializedName("gyhdList")
    val volunteerActivities: List<OneFormVolunteerActivity>? = null,
    @SerializedName("grryList")
    val honors: List<OneFormHonor>? = null,
    @SerializedName("dektcjXxList")
    val secondClassXxScores: List<OneFormSecondClassXxScore>? = null,
    @SerializedName("dektcjBxList")
    val secondClassBxScores: List<OneFormSecondClassBxScore>? = null
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

data class OneFormVolunteerActivity(
    @SerializedName("SORT")
    val sort: Int?,
    @SerializedName("HDMC")
    val activityName: String?,
    @SerializedName("HDSJ")
    val activityTime: String?,
    @SerializedName("ZZDW")
    val organizer: String?,
    @SerializedName("FWSC")
    val serviceHours: String?
)

data class OneFormHonor(
    @SerializedName("SORT")
    val sort: Int?,
    @SerializedName("HJMC")
    val name: String?,
    @SerializedName("HJDJ")
    val level: String?,
    @SerializedName("HJJB")
    val scope: String?,
    @SerializedName("HJJE")
    val amount: String?,
    @SerializedName("ND")
    val year: String?
)

data class OneFormSecondClassXxScore(
    @SerializedName("SORT")
    val sort: Int?,
    @SerializedName("GYFW")
    val publicService: String?,
    @SerializedName("SHSJ")
    val socialPractice: String?,
    @SerializedName("CYHD")
    val entrepreneurship: String?,
    @SerializedName("WYHD")
    val culturalActivity: String?,
    @SerializedName("JNXM")
    val skills: String?
)

data class OneFormSecondClassBxScore(
    @SerializedName("SORT")
    val sort: Int?,
    @SerializedName("SZXX")
    val qualityStudy: String?,
    @SerializedName("KJCX")
    val technologyInnovation: String?,
    @SerializedName("TYJS")
    val sports: String?,
    @SerializedName("LDSJ")
    val laborPractice: String?
)

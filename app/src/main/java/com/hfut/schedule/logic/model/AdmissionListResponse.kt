package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class AdmissionListResponse(val data : AdmissionListBean)

data class AdmissionListBean(
    // Map Key=省 Value=AdmissionMapBean()
    @SerializedName("ssmc_nf_zslx_sex_campus_klmc_Map") val list : Map<String, List<AdmissionMapBean>>
)

data class AdmissionMapBean(
    @SerializedName("klmc")
    val subject : String, // 科类
    val campus : String,
    @SerializedName("nf")
    val year : String,
    @SerializedName("zslx")
    val type : String // 类型
) {
    override fun toString() = "${year}年${campus}${type}${subject}"
}

data class AdmissionDetailResponsePlan(val data: AdmissionDetailBeanPlan)

data class AdmissionDetailResponseHistory(val data: AdmissionDetailBeanHistory)

sealed class AdmissionDetailBean {
    data class History(val data: AdmissionDetailBeanHistory) : AdmissionDetailBean()
    data class Plan(val data: AdmissionDetailBeanPlan) : AdmissionDetailBean()
}

data class AdmissionDetailBeanHistory(
    // 概况
    @SerializedName("zsSsgradeList")
    val generalSituationList : List<AdmissionGeneralSituationHistory>,
    @SerializedName("sszygradeList")
    val majorSituationList : List<AdmissionMajorSituationHistory>
)

data class AdmissionDetailBeanPlan(
    // 概况
    @SerializedName("zsjhTotal")
    val generalSituationList : List<AdmissionGeneralSituationPlan>,
    @SerializedName("zsjhList")
    val majorSituationList : List<AdmissionMajorSituationPlan>
)

abstract class AdmissionSituationHistory {
    abstract val minScore : Double?
    abstract val maxScore : Double?
    abstract val avgScore : Double?
    abstract val fsx : Double?

    fun scoreString(): String {
        return "最低分 "+ (minScore ?: "--") + " | 最高分 " + (maxScore ?: "--") + " | 平均分 " + (avgScore ?: "--")  + " | 控制线 " + (fsx ?: "--")
    }
}

data class AdmissionGeneralSituationHistory(
    override val minScore : Double?,
    override val maxScore : Double?,
    override val avgScore : Double?,
    override val fsx : Double?,
) : AdmissionSituationHistory()

data class AdmissionMajorSituationHistory(
    override val minScore : Double?,
    override val maxScore : Double?,
    override val avgScore : Double?,
    override val fsx : Double?,
    @SerializedName("zymc")
    val major : String
) : AdmissionSituationHistory()

abstract class AdmissionSituationPlan {
    abstract val remarks : String
}

data class AdmissionGeneralSituationPlan(
    override val remarks: String,

    @SerializedName("zsjhs")
    val count : Int?,
    @SerializedName("xkkm")
    val subjectRequirement : String,
    @SerializedName("sxkmyqzw")
    val firstSubjectRequirement : String?,
) : AdmissionSituationPlan()

data class AdmissionMajorSituationPlan(
    override val remarks: String,
    @SerializedName("zsjhs")
    val count : Int?,
    @SerializedName("xkkm")
    val subjectRequirement : String,
    @SerializedName("sxkmyqzw")
    val firstSubjectRequirement : String?,
    @SerializedName("zyxf")
    val fee : String,
    @SerializedName("zydhmc")
    val major : String
) : AdmissionSituationPlan()

data class AdmissionTokenResponse(
    @SerializedName("jessionid")
    val cookie : String,
    val data : String
)
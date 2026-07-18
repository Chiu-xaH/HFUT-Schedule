package com.hfut.schedule.network.api.model.response.json.admission

import com.google.gson.annotations.SerializedName

data class AdmissionDetailPlanResponse(
    val data: AdmissionDetailPlan
)

data class AdmissionDetailHistoryResponse(
    val data: AdmissionDetailHistory
)

sealed class AdmissionDetailBean {
    data class History(val data: AdmissionDetailHistory) : AdmissionDetailBean()
    data class Plan(val data: AdmissionDetailPlan) : AdmissionDetailBean()
}

data class AdmissionDetailHistory(
    // 概况
    @SerializedName("zsSsgradeList")
    val generalSituationList : List<AdmissionDetailGeneralSituationHistory>,
    @SerializedName("sszygradeList")
    val majorSituationList : List<AdmissionDetailMajorSituationHistory>
)

data class AdmissionDetailPlan(
    // 概况
    @SerializedName("zsjhTotal")
    val generalSituationList : List<AdmissionDetailGeneralSituationPlan>,
    @SerializedName("zsjhList")
    val majorSituationList : List<AdmissionDetailMajorSituationPlan>
)

abstract class AdmissionDetailSituationHistory {
    abstract val minScore : Double?
    abstract val maxScore : Double?
    abstract val avgScore : Double?
    abstract val fsx : Double?

    fun scoreString(): String {
        return "最低分 "+ (minScore ?: "--") + " | 最高分 " + (maxScore ?: "--") + " | 平均分 " + (avgScore ?: "--")  + " | 控制线 " + (fsx ?: "--")
    }
}

data class AdmissionDetailGeneralSituationHistory(
    override val minScore : Double?,
    override val maxScore : Double?,
    override val avgScore : Double?,
    override val fsx : Double?,
) : AdmissionDetailSituationHistory()

data class AdmissionDetailMajorSituationHistory(
    override val minScore : Double?,
    override val maxScore : Double?,
    override val avgScore : Double?,
    override val fsx : Double?,
    @SerializedName("zymc")
    val major : String
) : AdmissionDetailSituationHistory()

abstract class AdmissionDetailSituationPlan {
    abstract val remarks : String
}

data class AdmissionDetailGeneralSituationPlan(
    override val remarks: String,
    @SerializedName("zsjhs")
    val count : Int?,
    @SerializedName("xkkm")
    val subjectRequirement : String,
    @SerializedName("sxkmyqzw")
    val firstSubjectRequirement : String?,
) : AdmissionDetailSituationPlan()

data class AdmissionDetailMajorSituationPlan(
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
) : AdmissionDetailSituationPlan()

data class AdmissionTokenResponse(
    @SerializedName("jessionid")
    val cookie : String,
    val data : String
)

package com.hfut.schedule.network.api.model.response.json.admission

import com.google.gson.annotations.SerializedName

data class AdmissionListResponse(
    val data : AdmissionList
)

data class AdmissionList(
    // Map Key=省 Value=AdmissionMapBean()
    @SerializedName("ssmc_nf_zslx_sex_campus_klmc_Map")
    val map : Map<String, List<Admission>>
)

data class Admission(
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
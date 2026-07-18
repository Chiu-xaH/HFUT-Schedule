package com.hfut.schedule.network.api.model.response.json.one

import com.google.gson.annotations.SerializedName

data class OneFeeResponse(
    val data : OneFeeData?
)

data class OneFeeData(
    val total : String,
    // 体检费
    @SerializedName("dstjf")
    val physicalExaminationFee: String,
    // 住宿费
    @SerializedName("zsf")
    val dormitoryFee : String,
    // 学费
    @SerializedName("xf")
    val tuitionFee : String,
    // 军训费
    @SerializedName("dsjxf")
    val militaryTrainingFee : String
)

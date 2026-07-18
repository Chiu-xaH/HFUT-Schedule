package com.hfut.schedule.network.api.model.response.json.jxglstu.transfer

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class JxglstuTransferMajorResponse(
    val data : List<JxglstuTransferMajorData>
)

data class JxglstuTransferMajorData(
    val registrationConditions : String?,
    val id : Int,
    val changeMajorBatch : JxglstuTransferMajorInfo?,
    val department : MultiLanguageBaseData,
    val major : MultiLanguageBaseData,
    val preparedStdCount : Int,
    val applyStdCount : Int
)

data class JxglstuTransferMajorInfo(
    val nameZh : String,
    val applyStartTime : String,
    val applyEndTime : String,
    val submitStartTime : String,
    val submitEndTime : String,
    val enrollStartTime : String,
    val enrollEndTime : String,
    val inGrade : String,
    val bulletin : String?,
    val applyLimitCount : Int
)
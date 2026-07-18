package com.hfut.schedule.network.api.model.response.json.jxglstu.transfer

import com.hfut.schedule.network.api.model.response.json.jxglstu.transfer.JxglstuTransferMajorData

data class JxglstuTransferMajorMyApplyResponse(
    val models : List<JxglstuTransferMajorMyApply>
)

data class JxglstuTransferMajorMyApply(
    val changeMajorSubmit : JxglstuTransferMajorData,
    val applyStatus : String?,
    val id : Int
)
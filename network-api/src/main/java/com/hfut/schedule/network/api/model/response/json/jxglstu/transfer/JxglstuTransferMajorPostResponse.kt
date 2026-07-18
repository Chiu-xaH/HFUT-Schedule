package com.hfut.schedule.network.api.model.response.json.jxglstu.transfer

import com.hfut.schedule.network.api.model.response.json.jxglstu.JxglstuPostErrorMessage

data class JxglstuTransferMajorPostResponse(
    val result : Boolean,
    val errors : List<JxglstuPostErrorMessage>
)
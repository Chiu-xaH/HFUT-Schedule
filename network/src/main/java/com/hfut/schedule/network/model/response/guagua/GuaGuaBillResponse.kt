package com.hfut.schedule.network.model.response.guagua

data class GuaGuaBillResponse(
    val data : List<GuaguaBill>
)

data class GuaguaBill(
    val dealDate : String,
    val dealMark : String,
    val description : String,
    val xfMoney : Double?,
    val dealMoney : Double?
)


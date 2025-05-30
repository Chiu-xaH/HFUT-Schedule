package com.hfut.schedule.logic.model.guagua


data class GuaguaBillsResponse(
    val data : List<GuaguaBills>
)
data class GuaguaBills(
    val dealDate : String,
    val dealMark : String,
    val description : String,
    val xfMoney : Double?,
    val dealMoney : Double?
)


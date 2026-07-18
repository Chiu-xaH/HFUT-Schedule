package com.hfut.schedule.network.model.response.huixin

data class HuiXinRangeBillResponse (
    val data : HuiXinRangeBill
)

data class HuiXinRangeBill(
    val income : Float,
    val expenses : Float
)
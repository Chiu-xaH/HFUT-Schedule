package com.hfut.schedule.network.api.model.response.json.huixin

data class HuiXinRangeBillResponse (
    val data : HuiXinRangeBill
)

data class HuiXinRangeBill(
    val income : Float,
    val expenses : Float
)
package com.hfut.schedule.network.api.model.response.json.huixin

data class HuiXinMonthBillResponse(
    val data: Map<String, Double>
)

data class HuiXinMonthBill(
    val date : String,
    val balance : Double
)
package com.hfut.schedule.network.model.response.huixin

data class HuiXinMonthBillResponse(
    val data: Map<String, Double>
)

data class HuiXinMonthBill(
    val date : String,
    val balance : Double
)
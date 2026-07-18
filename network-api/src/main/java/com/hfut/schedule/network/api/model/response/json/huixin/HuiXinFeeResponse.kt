package com.hfut.schedule.network.api.model.response.json.huixin

data class HuiXinFeeResponse(
    val map : HuiXinFee
)

data class HuiXinFee(
    val showData : Map<String,String>
)


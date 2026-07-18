package com.hfut.schedule.network.model.response.huixin

data class HuiXinFeeResponse(
    val map : HuiXinFee
)

data class HuiXinFee(
    val showData : Map<String,String>
)


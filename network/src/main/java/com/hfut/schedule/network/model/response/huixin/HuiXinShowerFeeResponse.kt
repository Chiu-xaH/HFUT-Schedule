package com.hfut.schedule.network.model.response.huixin

data class HuiXinShowerFeeResponse(
    val map : HuiXinShowerFeeMap
)

data class HuiXinShowerFeeMap(
    val data : HuiXinShowerFee
)

data class HuiXinShowerFee(
    val telPhone : String,
    val identifier : String?,
    val name : String?,
    val accountMoney : Int,
    val accountGivenMoney : Int
)

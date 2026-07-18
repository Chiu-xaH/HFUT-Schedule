package com.hfut.schedule.network.api.model.response.json.guagua

data class GuaGuaLoginResponse(
    val data : GuaGuaLoginData?,
    val message: String
)

data class GuaGuaLoginData(
    val telPhone : String,
    val name : String,
    val accountMoney : Int,
    val accountGivenMoney : Int,
    val loginCode : String
)
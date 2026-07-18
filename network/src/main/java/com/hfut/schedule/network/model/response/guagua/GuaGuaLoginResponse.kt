package com.hfut.schedule.network.model.response.guagua

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
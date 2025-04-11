package com.hfut.schedule.logic.model.guagua


data class GuaGuaLoginResponse(val data : GuaGuaLogin)

data class GuaguaLoginMsg(val message: String?)



data class GuaGuaLogin(
    val telPhone : String,
    val name : String,
    val accountMoney : Int,
    val accountGivenMoney : Int,
    val loginCode : String
)
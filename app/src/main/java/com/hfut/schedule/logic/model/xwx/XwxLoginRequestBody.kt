package com.hfut.schedule.logic.model.xwx

data class XwxLoginRequestBody(
    val schoolCode : Long,
    val userId : String ,
    val password : String,
    val loginType : Int = 3,
)
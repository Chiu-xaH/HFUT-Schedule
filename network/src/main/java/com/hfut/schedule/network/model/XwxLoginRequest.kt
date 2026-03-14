package com.hfut.schedule.network.model

data class XwxLoginRequest(
    val schoolCode : Long,
    val userId : String ,
    val password : String,
    val loginType : Int = 3,
)
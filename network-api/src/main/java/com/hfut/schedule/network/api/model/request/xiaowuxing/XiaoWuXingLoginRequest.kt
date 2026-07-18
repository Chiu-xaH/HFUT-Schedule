package com.hfut.schedule.network.api.model.request.xiaowuxing

data class XiaoWuXingLoginRequest(
    val schoolCode : Long,
    val userId : String ,
    val password : String,
    val loginType : Int = 3,
)
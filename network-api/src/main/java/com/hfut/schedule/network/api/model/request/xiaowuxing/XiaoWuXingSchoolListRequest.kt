package com.hfut.schedule.network.api.model.request.xiaowuxing

data class XiaoWuXingSchoolListRequest(
    val schoolCode : String = "",
    val userId : String = "",
    val type : Int = 1
)
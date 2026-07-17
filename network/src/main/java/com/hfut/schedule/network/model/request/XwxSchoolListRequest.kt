package com.hfut.schedule.network.model.request

data class XwxSchoolListRequest(
    val schoolCode : String = "",
    val userId : String = "",
    val type : Int = 1
)
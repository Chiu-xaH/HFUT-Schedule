package com.hfut.schedule.network.model

data class XwxSchoolListRequest(
    val schoolCode : String = "",
    val userId : String = "",
    val type : Int = 1
)
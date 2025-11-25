package com.hfut.schedule.logic.model.xwx

data class XwxSchoolListRequestBody(
    val schoolCode : String = "",
    val userId : String = "",
    val type : Int = 1
)
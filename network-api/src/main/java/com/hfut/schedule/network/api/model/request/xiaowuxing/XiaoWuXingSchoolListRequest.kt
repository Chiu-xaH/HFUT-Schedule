package com.hfut.schedule.network.api.model.request.xiaowuxing

import com.xah.common.logic.util.EMPTY_STRING

data class XiaoWuXingSchoolListRequest(
    val schoolCode : String = EMPTY_STRING,
    val userId : String = EMPTY_STRING,
    val type : Int = 1
)
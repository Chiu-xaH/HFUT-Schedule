package com.hfut.schedule.network.api.model.response.dto

import com.xah.common.logic.util.EMPTY_STRING

data class SchoolNetInfo(
    val fee : String,
    val flow : String,
    val postJson : String = EMPTY_STRING
)
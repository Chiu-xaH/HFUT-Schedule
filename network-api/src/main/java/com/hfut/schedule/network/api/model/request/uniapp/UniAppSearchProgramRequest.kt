package com.hfut.schedule.network.api.model.request.uniapp

import com.xah.common.logic.util.EMPTY_STRING

data class UniAppSearchProgramRequest(
    val nameZhLike : String = EMPTY_STRING,
    val currentPage : Int ,
    val pageSize : Int
)
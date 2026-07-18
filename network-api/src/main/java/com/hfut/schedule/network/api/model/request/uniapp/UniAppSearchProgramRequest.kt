package com.hfut.schedule.network.api.model.request.uniapp

data class UniAppSearchProgramRequest(
    val nameZhLike : String = "",
    val currentPage : Int ,
    val pageSize : Int
)
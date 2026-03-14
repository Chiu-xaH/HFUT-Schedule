package com.hfut.schedule.network.model

data class UniAppSearchProgramRequest(
    val nameZhLike : String = "",
    val currentPage : Int ,
    val pageSize : Int
)
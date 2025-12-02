package com.hfut.schedule.logic.model.uniapp

import com.hfut.schedule.logic.model.jxglstu.NameZh

data class UniAppSearchProgramRequest(
    val nameZhLike : String = "",
    val currentPage : Int ,
    val pageSize : Int
)

data class UniAppSearchProgramResponse(
    val data : UniAppSearchProgramSubResponse
)
data class UniAppSearchProgramSubResponse(
    val data : List<UniAppSearchProgramBean>
)
data class UniAppSearchProgramBean(
    val id : Int,
    val nameZh: String,
    val grade : String,
    val department : NameZh,
    val major : NameZh,
)
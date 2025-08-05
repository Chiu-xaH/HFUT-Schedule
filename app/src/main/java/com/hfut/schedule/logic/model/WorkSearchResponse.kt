package com.hfut.schedule.logic.model

import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus

data class WorkSearchResponse(
    val data : List<WorkBean>
)

data class WorkBean(
    val type : String,
    val id : String,
    val title : String,
    val time : String
)

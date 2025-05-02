package com.hfut.schedule.logic.model

data class WorkSearchResponse(
    val data : List<WorkBean>
)
data class WorkBean(
    val type : String,
    val id : String,
    val title : String,
    val time : String
)

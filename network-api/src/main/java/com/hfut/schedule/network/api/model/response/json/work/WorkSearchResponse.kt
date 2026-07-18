package com.hfut.schedule.network.api.model.response.json.work

data class WorkSearchResponse(
    val data : List<Work>
)

data class Work(
    val type : String,
    val id : String,
    val title : String,
    val time : String
)

package com.hfut.schedule.network.model.response.community

data class CommunityStuAppResponse(
    val result : List<CommunityStuApp>
)

data class CommunityStuApp(
    val category : String,
    val subList : List<CommunityStuAppDetail>
)

data class CommunityStuAppDetail(
    val name : String,
    val logo : String,
    val url : String?
)
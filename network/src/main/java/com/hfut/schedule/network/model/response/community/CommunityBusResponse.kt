package com.hfut.schedule.network.model.response.community

data class CommunityBusResponse(
    val result : List<CommunityBus>
)

data class CommunityBus(
    val type : String,
    val from : String,
    val to : String,
    val time : String,
    val place : String,
    val stop : String,
    val count : Int
)

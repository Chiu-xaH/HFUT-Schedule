package com.hfut.schedule.logic.model.community

data class BusResponse(
    val result : List<BusBean>
)

data class BusBean(
    val type : String,
    val from : String,
    val to : String,
    val time : String,
    val place : String,
    val stop : String,
    val count : Int
)

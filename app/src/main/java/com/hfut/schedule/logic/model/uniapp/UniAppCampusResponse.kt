package com.hfut.schedule.logic.model.uniapp

data class UniAppCampusResponse(
    val data : List<UniAppCampusBean>
)

data class UniAppCampusBean(
    val nameZh : String,
    val id : Int
)

package com.hfut.schedule.network.api.model.response.json.uniapp

data class UniAppCampusResponse(
    val data : List<UniAppCampusBean>
)

data class UniAppCampusBean(
    val nameZh : String,
    val id : Int
)

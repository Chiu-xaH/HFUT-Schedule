package com.hfut.schedule.logic.model.uniapp

data class UniAppBuildingsResponse(
    val data : List<UniAppBuildingBean>
)

data class UniAppBuildingBean(
    val nameZh : String,
    val id : Int,
    val campusAssoc : Int,
)

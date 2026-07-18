package com.hfut.schedule.network.api.model.response.json.uniapp

data class UniAppBuildingResponse(
    val data : List<UniAppBuilding>
)

data class UniAppBuilding(
    val nameZh : String,
    val id : Int,
    val campusAssoc : Int,
)
package com.hfut.schedule.network.api.model.response.json.huixin

data class HuiXinHefeiBuildingResponse (
    val map : HuiXinHefeiBuildingMap
)

data class HuiXinHefeiBuildingMap(
    val data : List<HuiXinHefeiBuilding>
)

data class HuiXinHefeiBuilding(
    val name : String,
    val value : String
)



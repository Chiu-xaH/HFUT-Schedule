package com.hfut.schedule.logic.model

data class HuiXinHefeiBuildingsResponse (
    val map : HuiXinHefeiBuildings
)
data class HuiXinHefeiBuildings(
    val data : List<HuiXinHefeiBuildingBean>
)
data class HuiXinHefeiBuildingBean(
    val name : String,
    val value : String
)



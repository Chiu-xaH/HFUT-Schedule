package com.hfut.schedule.logic.model.community

data class MapResponse(
    val result : List<MapBean>
)

data class MapBean(
    val name : String,
    val currentMap : String,
    val nodeVOList : List<NodeV>
)
data class NodeV(val name : String)
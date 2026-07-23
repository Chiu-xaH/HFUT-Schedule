package com.hfut.schedule.network.api.model.response.html

data class FloorMap(
    val width: Float,
    val height: Float,
    val rooms: List<RoomRect>
)

data class RoomRect(
    val id: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

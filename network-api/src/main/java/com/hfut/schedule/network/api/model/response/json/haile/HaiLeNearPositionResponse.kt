package com.hfut.schedule.network.api.model.response.json.haile

data class HaiLeNearPositionResponse(
    val data : HaiLeNearPositionData
)

data class HaiLeNearPositionData(
    val items : List<HaiLeNearPositionBean>
)

data class HaiLeNearPositionBean(
    val id : Long,
    val name: String,
    val address : String,
    val workTime : String,
    val categoryCodeList : List<String>,
    val enableReserve : Boolean,
    val reserveNum : Int, // 可预约
    val idleCount : Int // 空
)

package com.hfut.schedule.network.api.model.response.json.haile

data class HaiLeDeviceDetailResponse(
    val data : HaiLeDeviceDetailData
)

data class HaiLeDeviceDetailData(
    val items : List<HaiLeDeviceDetailBean>
)

data class HaiLeDeviceDetailBean(
    val name: String,
    val state : Int,
    val finishTime : String?,
    val enableReserve : Boolean,
    val reserveNum : Int, // 可预约
)

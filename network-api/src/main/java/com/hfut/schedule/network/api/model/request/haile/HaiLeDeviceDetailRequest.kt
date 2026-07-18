package com.hfut.schedule.network.api.model.request.haile

import com.hfut.schedule.network.api.model.Constant

data class HaiLeDeviceDetailRequest(
    val positionId : String,
    val floorCode : String? = null,
    val categoryCode : String? = null,
    val page : Int,
    val pageSize : Int = Constant.DEFAULT_PAGE_SIZE,
)
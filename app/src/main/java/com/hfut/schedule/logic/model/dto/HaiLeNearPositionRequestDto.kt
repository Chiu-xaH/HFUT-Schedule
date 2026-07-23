package com.hfut.schedule.logic.model.dto

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.network.api.model.request.haile.HaiLeNearPositionRequest
import com.hfut.schedule.network.api.model.request.haile.HaiLeNearPositionRequestDto
import com.xah.common.logic.model.Campus

fun HaiLeNearPositionRequestDto.toRequestBody() : HaiLeNearPositionRequest {
    val location = MyApplication.campusLocations[campus]!!
    return HaiLeNearPositionRequest(
        lng = location.lng,
        lat = location.lat,
        categoryCode = categoryCode?.typeCode,
        page = page,
    )
}
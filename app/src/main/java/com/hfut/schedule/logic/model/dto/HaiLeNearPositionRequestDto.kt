package com.hfut.schedule.logic.model.dto

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.network.api.model.request.haile.HaiLeNearPositionRequest
import com.xah.common.logic.model.Campus

data class HaiLeNearPositionRequestDto(
    val campus : Campus,
    val categoryCode : HaiLeType? = null,
    val page: Int
) {
    fun toRequestBody() : com.hfut.schedule.network.api.model.request.haile.HaiLeNearPositionRequest {
        val location = MyApplication.campusLocations[campus]!!
        return HaiLeNearPositionRequest(
            lng = location.lng,
            lat = location.lat,
            categoryCode = categoryCode?.typeCode,
            page = page,
        )
    }
}

enum class HaiLeType(
    val typeCode : String,
    val description: String
) {
    WASHING_MACHINE("00","洗衣"),
    SHOES_WASHER("01","洗鞋"),
    CLOTHES_DRYER("02","烘干"),
}
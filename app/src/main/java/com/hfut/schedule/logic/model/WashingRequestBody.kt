package com.hfut.schedule.logic.model

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.network.model.HaiLeNearPositionRequest

enum class HaiLeType(val typeCode : String,val description: String) {
    WASHING_MACHINE("00","洗衣"),
    SHOES_WASHER("01","洗鞋"),
    CLOTHES_DRYER("02","烘干"),
}


data class HaiLeNearPositionRequestDTO(
    val campus : Campus,
    val categoryCode : HaiLeType? = null,
    val page: Int
) {
    fun toRequestBody() : HaiLeNearPositionRequest {
        val location = MyApplication.campusLocations[campus]!!
        return HaiLeNearPositionRequest(
            lng = location.lng,
            lat = location.lat,
            categoryCode = categoryCode?.typeCode,
            page = page,
        )
    }
}

data class Location(val lng : Double,val lat : Double)

data class HaiLeNearPositionResponse(val data : HaiLeNearPositionData)
data class HaiLeDeviceDetailResponse(val data : HaiLeDeviceDetailData)

data class HaiLeNearPositionData(val items : List<HaiLeNearPositionBean>)
data class HaiLeDeviceDetailData(val items : List<HaiLeDeviceDetailBean>)

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

data class HaiLeDeviceDetailBean(
    val name: String,
    val state : Int,
    val finishTime : String?,
    val enableReserve : Boolean,
    val reserveNum : Int, // 可预约
)

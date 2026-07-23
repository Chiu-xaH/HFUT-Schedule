package com.hfut.schedule.network.api.model.request.haile

import com.hfut.schedule.network.api.model.Constant
import com.xah.common.logic.model.Campus

data class HaiLeNearPositionRequest(
    val lng : Double,// 经度
    val lat : Double,// 纬度
    val categoryCode : String? = null, // 01洗衣机 ,02烘干机，03洗鞋机，不加或00全部
    val page : Int,
    val pageSize : Int = Constant.DEFAULT_PAGE_SIZE,
)

data class HaiLeNearPositionRequestDto(
    val campus : Campus,
    val categoryCode : HaiLeType? = null,
    val page: Int
)

enum class HaiLeType(
    val typeCode : String,
    val description: String
) {
    WASHING_MACHINE("00","洗衣"),
    SHOES_WASHER("01","洗鞋"),
    CLOTHES_DRYER("02","烘干"),
}
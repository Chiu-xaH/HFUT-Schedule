package com.hfut.schedule.network.model

import com.hfut.schedule.network.util.Constant

data class HaiLeNearPositionRequest(
    val lng : Double,// 经度
    val lat : Double,// 纬度
    val categoryCode : String? = null, // 01洗衣机 ,02烘干机，03洗鞋机，不加或00全部
    val page : Int,
    val pageSize : Int = Constant.DEFAULT_PAGE_SIZE,
)
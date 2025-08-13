package com.hfut.schedule.logic.model

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.CampusDetail

data class HaiLeNearPositionRequestBody(
    val lng : Double,// 经度
    val lat : Double,// 纬度
     val categoryCode : String? = null, // 01洗衣机 ,02烘干机，03洗鞋机，不加或00全部
     val page : Int,
     val pageSize : Int = prefs.getString("HaileRequest", MyApplication.PAGE_SIZE.toString())?.toIntOrNull() ?: MyApplication.PAGE_SIZE,
)

data class HaiLeDeviceDetailRequestBody(
    val positionId : String,
    val floorCode : String? = null,
     val categoryCode : String? = null,
     val page : Int,
     val pageSize : Int = prefs.getString("HaileRequest", MyApplication.PAGE_SIZE.toString())?.toIntOrNull() ?: MyApplication.PAGE_SIZE,
)
data class HaiLeTradeListRequestBody(
    val newOrderState : String? = null,
    val page : Int,
    val pageSize : Int = prefs.getString("HaileRequest", MyApplication.PAGE_SIZE.toString())?.toIntOrNull() ?: MyApplication.PAGE_SIZE,
)

enum class HaiLeType(val typeCode : String,val description: String) {
    WASHING_MACHINE("00","洗衣"),
    SHOES_WASHER("01","洗鞋"),
    CLOTHES_DRYER("02","烘干"),
}


data class HaiLeNearPositionRequestDTO(
    val campus : CampusDetail,
    val categoryCode : HaiLeType? = null,
    val page: Int
) {
    fun toRequestBody() : HaiLeNearPositionRequestBody {
        val location = MyApplication.campusLocations[campus]!!
        return HaiLeNearPositionRequestBody(
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
data class HaiLeTradeListData(val items : List<HaiLeTradeBean>)

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

data class HaiLeTradeBean(
    val createTime : String,
    val realPrice : String,
    val stateDesc : String,
    val orderItemList : List<HaiLeTradeOrderBean>
)
data class HaiLeTradeOrderBean(
    val realPrice : String?,
    val goodsName : String?,
    val goodsItemName : String?,
    val categoryCode : String?,
    val unit : String?
)
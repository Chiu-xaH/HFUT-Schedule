package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.model.zjgd.BillBean
import com.hfut.schedule.logic.model.zjgd.BillResponse

data class VercelForecastRequestBody(
    val date : String,
    val amount : String,
    val merchant : String
)
data class VercelForecastRequestBodySimple(
    val date : String,
    val amount : Double,
)

data class ForecastResponse(
    val data : ForecastAllBean,
    val state : Int,
    val msg : String?
)
data class ForecastAllBean(
    val day : ForecastBean,
    val month : ForecastBean
)
data class ForecastBean(
    @SerializedName("predicted_data") val predictedData : Double,
    @SerializedName("average_data") val averageData : Double,
    @SerializedName("statistical_data") val statisticalData : List<VercelForecastRequestBodySimple>,
)

fun toVercelForecastRequestBody(originList : BillBean) : List<VercelForecastRequestBody> = originList.records.mapNotNull { record ->
    if(record.turnoverType == "消费") {
        val r = (record.tranamt ?: 0 ) / 100.0
        VercelForecastRequestBody(
            date = record.jndatetimeStr,
            amount = r.toString(),
            merchant = record.resume
        )
    } else {
        null
    }
}
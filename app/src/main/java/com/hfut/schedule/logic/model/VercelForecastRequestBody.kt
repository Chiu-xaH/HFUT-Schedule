package com.hfut.schedule.logic.model


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
    val predictedData : Double,
    val averageData : Double,
    val statisticalData : List<VercelForecastRequestBodySimple>,
)


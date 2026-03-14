package com.xah.forecast.model.network

data class VercelForecastRequestBody(
    val date : String,
    val amount : String,
    val merchant : String
)
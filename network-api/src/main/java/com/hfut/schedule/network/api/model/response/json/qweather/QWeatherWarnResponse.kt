package com.hfut.schedule.network.api.model.response.json.qweather

data class QWeatherWarnResponse(
    val warning : List<QWeatherWarning>
)

data class QWeatherWarning(
    val title : String,
    val typeName : String,
    val text : String
)
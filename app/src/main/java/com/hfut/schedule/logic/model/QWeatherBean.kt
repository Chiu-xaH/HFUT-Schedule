package com.hfut.schedule.logic.model

data class QWeatherResponse(
    val now : QWeatherNowBean
)

data class QWeatherNowBean(
    val temp : String, //温度
    val feelsLike : String, //体感温度
    val text : String,//多云
    val windDir : String, //西北风
    val windScale : String, //风级数
    val humidity : String, //湿度
    val icon : String//图标代码
)

data class QWeatherWarnResponse(
    val warning : List<QWeatherWarnBean>
)

data class QWeatherWarnBean(
    val title : String,
    val typeName : String,
    val text : String
)
package com.hfut.schedule.network.api.model.response.json.qweather

data class QWeatherNowResponse(
    val now : QWeatherNow
)

data class QWeatherNow(
    val temp : String, //温度
    val feelsLike : String, //体感温度
    val text : String,//多云
    val windDir : String, //西北风
    val windScale : String, //风级数
    val humidity : String, //湿度
    val icon : String//图标代码
)
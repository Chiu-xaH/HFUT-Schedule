package com.hfut.schedule.network.api.model.response.html

/**
 * @param place 上车地点
 * @param count 车辆数
 * @param week 发车week
 * @param time 发车时间
 * @param from 始发
 * @param to 终到
 * @param stops 途径
 */
data class Bus(
    val place : String,
    val count : Int,
    val week : String,
    val time : String,
    val from : String,
    val to : String,
    val stops : List<String>,
)

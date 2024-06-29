package com.hfut.schedule.logic.datamodel

data class ServerResponse(val Schedule : List<newSchedule>, val Wangke : List<newSchedule>)

data class newSchedule(
    val title : String,
    val info: String,
    val remark : String,
    var startTime : String,
    var endTime : String
)
data class UserInfo(val name : String?,
                    val studentID : String?,
                    val dateTime : String,
                    val appVersionName : String,
                    val deviceName : String?,
                    val systemVersion : Int)
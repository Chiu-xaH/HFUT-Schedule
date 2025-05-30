package com.hfut.schedule.logic.model

data class ServerResponse(val Schedule : List<newSchedule>, val Wangke : List<newSchedule>)

data class newSchedule(
    val title : String,
    val info: String,
    val remark : String,
    var startTime : String,
    var endTime : String,
    val showPublic : Boolean?
)
data class UserInfo(val name : String?,
                    val studentID : String?,
                    val dateTime : String,
                    val appVersionName : String,
                    val deviceName : String?,
                    val systemVersion : Int)
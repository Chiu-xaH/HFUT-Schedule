package com.hfut.manage.datamodel.data

data class APIResponse(val Schedule : List<Schedule>,val Wangke : List<Schedule>)

data class Schedule(
    val title : String,
    val info: String,
    val remark : String,
    var startTime : String,
    var endTime : String
)

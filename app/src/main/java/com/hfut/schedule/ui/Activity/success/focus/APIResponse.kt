package com.hfut.schedule.ui.Activity.success.focus

data class APIResponse(val Schedule : List<newSchedule>,val Wangke : List<newSchedule>)

data class newSchedule(
    val title : String,
    val info: String,
    val remark : String,
    var startTime : String,
    var endTime : String
)

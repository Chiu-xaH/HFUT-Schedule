package com.hfut.schedule.network.api.model.response.json.jxglstu.lesson

data class JxglstuCourseTimeResponse(
    val result: JxglstuCourseTimeResult
)

data class JxglstuCourseTimeResult(
    val courseUnitList : List<JxglstuCourseTime>
)

data class JxglstuCourseTime(
    val nameZh : String,
    val startTime : Int,
    val endTime : Int,
    val endTimeText : String,
    val startTimeText : String
)
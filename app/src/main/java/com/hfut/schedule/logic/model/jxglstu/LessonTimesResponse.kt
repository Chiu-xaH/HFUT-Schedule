package com.hfut.schedule.logic.model.jxglstu

data class LessonTimesResponse(
    val result: LessonTimesBean
)
data class LessonTimesBean(
    val courseUnitList : List<CourseUnitBean>
)
data class CourseUnitBean(
    val nameZh : String,
    val startTime : Int,
    val endTime : Int,
    val endTimeText : String,
    val startTimeText : String
)
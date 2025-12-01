package com.hfut.schedule.logic.model.uniapp

import com.hfut.schedule.logic.model.jxglstu.NameZh

data class UniAppCoursesResponse(
    val data : List<UniAppCourseBean>
)

data class UniAppCourse(
    val nameZh: String,
    val credits : Double
)

data class UniAppCourseBean(
    val id : Long,
    val code : String,
    val course : UniAppCourse,
    val stdCount : Int,
    val openDepartment : NameZh,
    val courseType : NameZh,
    val teacherAssignmentList : List<String>,
    val schedules : List<UniAppSchedule>
)

data class UniAppSchedule(
    val date : String,
    val weekday : Int,
    val startTime : Int,
    val endTime : Int,
    val teacherName : String,
    val room : NameZh?,
    val weekIndex : Int
)

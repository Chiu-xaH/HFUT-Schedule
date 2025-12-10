package com.hfut.schedule.logic.model.uniapp

import com.hfut.schedule.logic.model.jxglstu.NameZh

data class UniAppCoursesResponse(
    val data : List<UniAppCourseBean>
)

data class UniAppCourse(
    val nameZh: String,
    val credits : Double
)

data class UniAppCourseBean (
    override val id : Long,
    override val code : String,
    override val course : UniAppCourse,
    override val stdCount : Int,
    override val openDepartment : NameZh,
    override val courseType : NameZh,
    override val teacherAssignmentList : List<String>,
    override val schedules : List<UniAppSchedule>
) : UniAppBaseCourseBean()


abstract class UniAppBaseCourseBean {
    abstract val id : Long
    abstract val code : String
    abstract val course : UniAppCourse
    abstract val stdCount : Int
    abstract val openDepartment : NameZh
    abstract val courseType : NameZh
    abstract val teacherAssignmentList : List<String>
    abstract val schedules : List<UniAppSchedule>
}

data class UniAppSchedule(
    val date : String,
    val weekday : Int,
    val startTime : Int,
    val endTime : Int,
    val teacherName : String,
    val room : NameZh?,
    val weekIndex : Int
)

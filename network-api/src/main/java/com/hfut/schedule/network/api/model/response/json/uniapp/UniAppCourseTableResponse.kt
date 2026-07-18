package com.hfut.schedule.network.api.model.response.json.uniapp

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class UniAppCourseTableResponse(
    val data : List<UniAppCourse>
)

data class UniAppCourseInfo(
    val nameZh: String,
    val credits : Double
)

data class UniAppCourse (
    override val id : Long,
    override val code : String,
    override val course : UniAppCourseInfo,
    override val stdCount : Int,
    override val openDepartment : MultiLanguageBaseData,
    override val courseType : MultiLanguageBaseData,
    override val teacherAssignmentList : List<String>,
    override val schedules : List<UniAppSchedule>
) : UniAppBaseCourse()


abstract class UniAppBaseCourse {
    abstract val id : Long
    abstract val code : String
    abstract val course : UniAppCourseInfo
    abstract val stdCount : Int
    abstract val openDepartment : MultiLanguageBaseData
    abstract val courseType : MultiLanguageBaseData
    abstract val teacherAssignmentList : List<String>
    abstract val schedules : List<UniAppSchedule>
}

data class UniAppSchedule(
    val date : String,
    val weekday : Int,
    val startTime : Int,
    val endTime : Int,
    val teacherName : String,
    val room : MultiLanguageBaseData?,
    val weekIndex : Int
)

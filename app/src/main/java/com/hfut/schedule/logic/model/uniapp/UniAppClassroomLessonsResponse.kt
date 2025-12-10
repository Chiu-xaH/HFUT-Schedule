package com.hfut.schedule.logic.model.uniapp

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.model.jxglstu.NameZh

data class UniAppClassroomLessonsResponse(
    val data : List<UniAppClassroomLessonBean>
)

data class UniAppClassroomLessonBean(
    @SerializedName("nameZh")
    val className : String,
    override val id : Long,
    override val code : String,
    override val course : UniAppCourse,
    override val stdCount : Int,
    override val openDepartment : NameZh,
    override val courseType : NameZh,
    override val teacherAssignmentList : List<String>,
    override val schedules : List<UniAppSchedule>
) : UniAppBaseCourseBean()
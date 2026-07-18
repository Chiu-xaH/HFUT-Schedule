package com.hfut.schedule.network.api.model.response.json.uniapp

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class UniAppClassroomCourseTableResponse(
    val data : List<UniAppClassroomCourse>
)

data class UniAppClassroomCourse(
    @SerializedName("nameZh")
    val className : String,
    override val id : Long,
    override val code : String,
    override val course : UniAppCourseInfo,
    override val stdCount : Int,
    override val openDepartment : MultiLanguageBaseData,
    override val courseType : MultiLanguageBaseData,
    override val teacherAssignmentList : List<String>,
    override val schedules : List<UniAppSchedule>
) : UniAppBaseCourse()
package com.hfut.schedule.network.api.model.response.json.jxglstu.lesson

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class JxglstuCourseTableResponse(
    val result: JxglstuCourseTable
)

data class JxglstuCourseTable(
    val lessonList : List<JxglstuCourseTableLesson>,
    val scheduleList : List<JxglstuCourseTableSchedule>,
    val scheduleGroupList: List<JxglstuCourseTableScheduleGroup>
)

data class JxglstuCourseTableLesson(
    val courseName : String,
    val name : String,
    val id : String,
    val suggestScheduleWeekInfo : String,
    val courseTypeName : String,
    val remark : String?,
    val teacherAssignmentList : List<JxglstuCourseTableTeacherAssignment>
)

data class JxglstuCourseTableSchedule(
    val lessonId: Int,
    val room : MultiLanguageBaseData?,
    val weekday : Int,
    val personName : String,
    val weekIndex : Int,
    val startTime : Int,
    val periods : Int,
    val endTime : Int,
    val date : String,
    val lessonType : String
)

data class JxglstuCourseTableScheduleGroup(
    val lessonId: Int,
    val stdCount : Int
)

data class JxglstuCourseTableTeacherAssignment(
    val code : String,
    val name : String,
    val age : Int?,
    val titleName : String?
)
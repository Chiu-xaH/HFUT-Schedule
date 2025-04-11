package com.hfut.schedule.logic.model

data class TeacherResponse(val teacherData : List<TeacherBean>)
data class TeacherBean(
    val name : String,
    val url : String,
    val picUrl : String
)

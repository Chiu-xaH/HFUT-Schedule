package com.hfut.schedule.logic.datamodel

data class TeacherResponse(val teacherData : List<TeacherBean>)
data class TeacherBean(
    val name : String,
    val url : String,
    val picUrl : String
)

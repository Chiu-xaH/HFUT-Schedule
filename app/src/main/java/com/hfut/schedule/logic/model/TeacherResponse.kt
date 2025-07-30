package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class TeacherResponse(val teacherData : List<TeacherBean>)
data class TeacherBean(
    val name : String,
    val url : String,
    val picUrl : String,
    val sex : String,
    val gtutor : String,
    val doctorTutor : String,
    @SerializedName("prorank")
    val job : String,
    @SerializedName("collegeName")
    val department : String
)

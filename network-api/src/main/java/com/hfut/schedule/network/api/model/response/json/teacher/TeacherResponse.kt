package com.hfut.schedule.network.api.model.response.json.teacher

import com.google.gson.annotations.SerializedName

data class TeacherResponse(
    val teacherData : List<TeacherData>
)

data class TeacherData(
    val name : String,
    val url : String,
    val picUrl : String,
    val sex : String,
    @SerializedName("gtutor")
    val tutor : String,
    val doctorTutor : String,
    @SerializedName("prorank")
    val job : String,
    @SerializedName("collegeName")
    val department : String
)

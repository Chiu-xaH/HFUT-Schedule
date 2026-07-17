package com.hfut.schedule.logic.model.jxglstu


data class GradeJxglstuDTO(
    val term : String,
    val list : List<GradeJxglstuResponse>
)

data class GradeJxglstuResponse(
    val courseName : String,
    val credits : String,
    val gpa : String,
    val score : String,
    val detail : String,
    val lessonCode: String
)

package com.hfut.schedule.network.api.model.response.html


data class JxglstuTermGrade(
    val term : String,
    val list : List<JxglstuGrade>
)

data class JxglstuGrade(
    val courseName : String,
    val credits : String,
    val gpa : String,
    val score : String,
    val detail : String,
    val lessonCode: String
)

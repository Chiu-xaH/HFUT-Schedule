package com.hfut.schedule.logic.model.jxglstu

data class CourseBookResponse(val textbookAssignMap : Map<String,CourseBookBean>)

data class CourseBookBean(
    val textbook : String,
    val specialTextbook : String
)
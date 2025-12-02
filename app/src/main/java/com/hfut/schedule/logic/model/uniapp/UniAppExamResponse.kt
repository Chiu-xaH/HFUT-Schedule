package com.hfut.schedule.logic.model.uniapp

import com.hfut.schedule.logic.model.jxglstu.NameZh

data class UniAppExamResponse(
    val data : List<UniAppExamBean>
)

data class UniAppExamBean(
    val courseNameZh : String,
    val courseCode : String,
    val examType : NameZh,
    val examDate : String,
    val startTime : Int,
    val endTime : Int,
    val weekDay : Int,
    val place : String?
)

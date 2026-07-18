package com.hfut.schedule.network.api.model.response.json.uniapp

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class UniAppExamResponse(
    val data : List<UniAppExam>
)

data class UniAppExam(
    val courseNameZh : String,
    val courseCode : String,
    val examType : MultiLanguageBaseData,
    val examDate : String,
    val startTime : Int,
    val endTime : Int,
    val weekDay : Int,
    val place : String?
)

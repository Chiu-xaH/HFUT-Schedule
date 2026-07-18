package com.hfut.schedule.network.api.model.response.json.uniapp

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

data class UniAppGradeResponse(
    val data : List<UniAppGrade>
)

data class UniAppGrade(
    val courseNameZh : String,
    val lessonCode : String,
    val semester : MultiLanguageBaseData,
    val passed : Boolean,
    val finalGrade : String?,
    val gradeDetail : String,
    val credits : Double,
    val gp : Double
)
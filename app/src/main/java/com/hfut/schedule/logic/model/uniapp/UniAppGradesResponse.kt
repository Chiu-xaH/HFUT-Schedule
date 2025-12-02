package com.hfut.schedule.logic.model.uniapp

import com.hfut.schedule.logic.model.jxglstu.NameZh

data class UniAppGradesResponse(
    val data : List<UniAppGradeBean>
)


data class UniAppGradeBean(
    val courseNameZh : String,
    val lessonCode : String,
    val semester : NameZh,
    val passed : Boolean,
    val finalGrade : String?,
    val gradeDetail : String,
    val credits : Double,
    val gp : Double
)
package com.hfut.schedule.logic.datamodel.Community

data class GradeResponse(val result : GradeResult)

data class GradeResult(val gpa : Double,val classRanking : String,val majorRanking : String,val scoreInfoDTOList : List<scoreInfoDTOList>)

data class scoreInfoDTOList(val courseName : String,val score : Double,val credit : Double,val gpa : Float,val pass : Boolean)

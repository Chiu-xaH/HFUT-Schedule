package com.hfut.schedule.logic.model.community

data class GradeResponse(val result : GradeResult)

data class GradeResult(val gpa : Double,
                       val classRanking : String,
                       val majorRanking : String,
                       val scoreInfoDTOList : List<scoreInfoDTOList>)

data class scoreInfoDTOList(val courseName : String,
                            val score : Double,
                            val credit : Double,
                            val gpa : Float,
                            val pass : Boolean)

data class GradeJxglstuDTO(val term : String,val list : List<GradeResponseJXGLSTU>)

data class GradeResponseJXGLSTU(val title : String,
                                val score : String,
                                val GPA : String,
                                val grade : String,
                                val totalGrade : String,
                                val code: String
)

data class GradeAvgResponse(val result : AvgResult)

data class AvgResult(val myAvgScore : Double?,
                     val myAvgGpa : Double?,
                     val majorAvgScore : Double?,
                     val majorAvgGpa : Double?,
                     val majorAvgScoreRanking : String?,
                     val majorAvgGpaRanking : String?)

data class GradeAllResponse(val result : List<GradeAllResult> )
data class GradeAllResult(val myAvgScore : Double?,
                          val myAvgGpa : Double?,
                          val majorAvgScore : Double?,
                          val majorAvgGpa : Double?,
                          val maxAvgScore : Double?,
                          val maxAvgGpa : Double?)


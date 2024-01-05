package com.hfut.schedule.logic.datamodel.Community

data class CourseTotalResponse(val result : CourseResult)

data class CourseResult(val courseBasicInfoDTOList : List<courseBasicInfoDTOList>,
                        val startTime : List<String>,
                        val endTime : List<String>,
                        val xn : String,
                        val xq : String,
                        val start : String,
                        val end : String,
                        val totalWeekCount : Int,
                        val currentWeek : Int)

data class courseBasicInfoDTOList(val courseName : String,
                                  val credit : Double,
                                  val className : String,
                                  val trainingCategoryName_dictText : String,
                                  val courseDetailDTOList : List<courseDetailDTOList>)

data class courseDetailDTOList(val section : Int,
                               val sectionCount : Int,
                               val place : String,
                               val teacher : String,
                               val classTime : String,
                               val weekCount : List<Int>,
                               val week : Int,
                               var name : String)
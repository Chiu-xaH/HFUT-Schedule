package com.hfut.schedule.logic.datamodel.Jxglstu

data class SelectCourse(val id : Int,
                        val name : String,
                        val bulletin : String,
                        val selectDateTimeText : String,
                        val addRulesText : List<String>)


data class SelectCourseInfo(val id : Int,
                            val code : String,
                            val nameZh : String,
                            val teachers : List<courseType>,
                            val course: course,
                            val courseType : courseType,
                            val examMode : courseType,
                            val limitCount : Int,
                            val remark : String?,
                            val dateTimePlace : dateTimePlacePersonText)

data class SelectPostResponse(val errorMessage : errorMessage?,
                              val success : Boolean)
data class errorMessage(val textZh : String)
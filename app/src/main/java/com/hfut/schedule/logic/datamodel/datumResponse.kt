package com.hfut.schedule.logic.datamodel

data class data (val result: result)

data class result(val lessonList : List<lessonList>,
                  val scheduleList : List<scheduleList>,
                  val scheduleGroupList: List<scheduleGroupList>)

data class lessonList(val courseName : String,
                      val id : String,
                      val suggestScheduleWeekInfo : String,
                      val courseTypeName : String)

data class scheduleList(val lessonId: Int,
                        val room : room,
                        val weekday : Int,
                        val personName : String,
                        val weekIndex : Int,
                        val startTime : Int,
                        val periods : Int,
                        var endTime : Int,
                        val date : String,
                        var lessonType : String)

data class room(val nameZh : String)

data class scheduleGroupList(val lessonId: Int,
                             val stdCount : Int)

package com.hfut.schedule.logic.datamodel

import java.sql.Date
import java.sql.Time


data class result(val lessonList : List<lessonList>,
                  val scheduleList : List<scheduleList>,
                  val scheduleGroupList: List<scheduleGroupList>)

data class lessonList(val name : String,
                      val courseName : String,
                      val suggestScheduleWeekInfo : String, val id : Int)

data class scheduleList(val lessonId: Int,
                        val date: Date,
                        val room : room,
                        val weekday : Int,
                        val startTime: Time,
                        val endTime: Time,
                        val personName : String,
                        val weekIndex : Int)

data class room(val nameZh : String)

data class scheduleGroupList(val lessonId: Int,
                             val stdCount : Int)

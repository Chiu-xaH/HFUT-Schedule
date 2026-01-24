package com.hfut.schedule.logic.model
data class MyAPIResponse (val SettingsInfo : SettingsInfo,
                          val Lessons : Lessons,
                          val semesterId : String,
                          val TimeStamp : String,
                          val Labs : List<Lab>,
                          val Notifications : List<Notifications>,
                          val SchoolCalendar : String,
                          val Next : Boolean,
                          val API : String,
                          val useCaptcha : Boolean,
                          val startDay : String
                        )
data class SettingsInfo(val title : String,
                        val info : String,
                        val show : Boolean,
                        val celebration : Boolean)

data class Lessons(val MyList : List<Schedule>,
                   val Schedule : List<Schedule>)


data class Schedule(
    val time : String,
    val title : String,
    val info: String,
    var startTime : List<Int>,
    var endTime : List<Int>,
    val showPublic : Boolean)

data class Notifications(val title : String,
                         val info : String,
                         val remark : String,
                         val url : String?,
    val id : Int
)

data class Lab(val title : String,
               val info : String,
               val type : String)

data class Update(val version : String?,val text : String?)

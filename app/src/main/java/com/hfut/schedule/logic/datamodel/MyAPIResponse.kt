package com.hfut.schedule.logic.datamodel
data class MyAPIResponse (val SettingsInfo : SettingsInfo,
                          val Lessons : Lessons,
                          val semesterId : String,
                          val TimeStamp : String,
                          val Labs : List<Lab>,
                          val Notifications : List<Notifications>)
data class SettingsInfo(val version : String,
                        val title : String,
                        val info : String,
                        val celebration : Boolean)

data class Lessons(val MyList : List<MyList>,
                   val Schedule : List<Schedule>)

data class MyList(
    val time : String,
    val title : String,
    val info: String,
    var startTime : List<Int>,
    var endTime : List<Int>)

data class Schedule(
    val time : String,
    val title : String,
    val info: String,
    var startTime : List<Int>,
    var endTime : List<Int>)

data class Notifications(val title : String,
                         val info : String,
                         val remark : String)

data class Lab(val title : String,
               val info : String,
               val type : String)

data class Update(val version : String?,val text : String?)

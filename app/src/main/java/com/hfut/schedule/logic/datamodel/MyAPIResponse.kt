package com.hfut.schedule.logic.datamodel
data class MyAPIResponse (val SettingsInfo : SettingsInfo,
                          val Lessons : Lessons,
                          val semesterId : String,
                          val Notifications : List<Notifications>)
data class SettingsInfo(val version : String,
                        val title : String,
                        val info : String)

data class Lessons(val MyList : List<MyList>,
                   val Schedule : List<Schedule>)

data class MyList(
    val time : String,
    val title : String,
    val info: String)

data class Schedule(
    val time : String,
    val title : String,
    val info: String)

data class Notifications(val title : String,
                         val info : String,
                         val remark : String)


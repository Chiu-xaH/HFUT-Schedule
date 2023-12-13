package com.hfut.schedule.logic.datamodel
data class data4 (val SettingsInfo : SettingsInfo,
                  val Lessons : Lessons,
                  val semesterId : String)
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


package com.hfut.manage.datamodel.data

data class APIData (val SettingsInfo : SettingsInfo,
                    val Lessons : Lessons,
                    val semesterId : String,
                    //val TimeStamp : String,
                    val Labs : List<Lab>,
                    val Notifications : List<Notifications>,
                    val SchoolCalendar : String,
                    val Next : Boolean)

data class SettingsInfo(val version : String,
                        val title : String,
                        val info : String,
                        val show : Boolean,
                        val celebration : Boolean)

data class Lessons(val MyList : List<MyList>,
                   val Schedule : List<Schedule>)

data class MyList(
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


package com.hfut.schedule.logic.beans.community

data class TodayResponse(val result : TodayResult)

data class TodayResult(val todayCourse : todayCourse,
                       val bookLending : bookLending,
                       val todayExam : todayExam,
                       val todayActivity : todayActivity)

data class todayCourse(val startTime : String?,
                       val endTime : String?,
                       val place : String?,
                       val courseName : String?,
                       val className : String?)
data class bookLending(val bookName : String?,
                       val outTime : String?,
                       val dueTime : String?,
                       val returnTime : String?)
data class todayActivity(val activitySubject : String?,
                         val activityName : String?,
                         val startTime : String?,
                         val endTime : String?,
                         val qrCodeUrl : String?,
                         val activitySubject_dictText : String?)
data class todayExam(val courseName : String?,
                     val place : String?,
                     val startTime: String?,
                     val endTime: String?)
package com.hfut.schedule.logic.beans.community

data class ExamResponse(val result : ExamResult)

data class ExamResult(val examArrangementList : List<examArrangementList>)

data class examArrangementList(val courseName : String?,
                               val place : String?,
                               val formatStartTime : String?,
                               val formatEndTime : String?)
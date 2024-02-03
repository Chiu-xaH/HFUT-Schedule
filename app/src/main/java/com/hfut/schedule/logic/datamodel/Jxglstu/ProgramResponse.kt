package com.hfut.schedule.logic.datamodel.Jxglstu

data class ProgramResponse(val children : List<ProgramResponse>?,
                           val type : Type?,
                           val requireInfo : RequireInfo?,
                           val remark : String?,
                           val reference : Boolean,
                           val planCourses : List<PlanCourses>?,
                           //val periodInfoRatio : PeriodInfoRatio,
                           val sumChildrenNum : Int,
                           val sumChildrenRequiredCredits : Double,
                           val numBySubModule : Map<String,Int>,
                           val creditBySubModule : Map<String,Double>,
                           val creditByModuleType : Map<String,Double>,
                           val numByModuleType : Map<String,Int>,
                           val sumChildrenRequiredSubModuleNum : Int,
                           val sumChildrenRequiredCourseNum : Int,
                           val sumPlanCourseCredits : Double,
                           val sumPlanCourseNum : Int
)

data class Type(val nameZh : String)

data class RequireInfo(val requiredSubModuleNum : Double,
                       val requiredCourseNum : Int,
                       val totalTheoryPeriods : Int,
                       val totalTheoryCredits : Double,
                       val totalPracticePeriods : Int,
                       val totalPracticeCredits : Double,
                       val requiredTotalPeriods : Int,)

data class PlanCourses(val readableTerms : List<String>,
                       val readableSuggestTerms : List<String>,val remark : String,
)
//readableTerms开课学期，readableSuggestTerms建议修读学期

//data class PeriodInfoRatio()



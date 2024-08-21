package com.hfut.schedule.logic.datamodel.Jxglstu

data class ProgramResponse(val children : List<ProgramResponse>,
                           val type : Type?,
                           val requireInfo : RequireInfo?,
                           val remark : String?,
                           val reference : Boolean,
                           val planCourses : List<PlanCourses>,
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
                       val requiredCredits : Double?,
                       val totalPracticePeriods : Int,
                       val totalPracticeCredits : Double,
                       val requiredTotalPeriods : Int,)

data class PlanCourses(val readableTerms : List<Int>,
                       val readableSuggestTerms : List<String>,
                       val remark : String,val course : course,
                       val openDepartment : courseType
)
//readableTerms开课学期，readableSuggestTerms建议修读学期

//data class PeriodInfoRatio()

data class InProgramResponse(val ProgramShow : List<ProgramShow>)

data class ProgramPartOne(val type : String?,
                          val requiedCredits : Double?,
                          val partChildren : List<ProgramResponse?>,
                          val partCourse : List<PlanCourses>)
data class ProgramPartTwo(val type : String?,
                          val requiedCredits : Double?,
                          val part : List<PlanCourses>)
data class ProgramPartThree(val term : Int?,
                            val name : String,
                            val credit : Double?,
                            val depart :String)
data class ProgramShow(val name : String,
                       val type : String?,
                       val credit : Double?,
                       val term : List<Int?>,
                       val studyMust : Boolean?,
                       val school : String?,
                       val remark : String?)

data class ProgramCompletionResponse(val total : item,val other : List<item>)
data class item(val name : String,val actual : Double,val full : Double)




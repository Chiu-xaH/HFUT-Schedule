package com.hfut.schedule.logic.model.jxglstu

import com.hfut.schedule.R


data class ProgramListBean(
    val id : Int,
    val grade : String,
    val name : String,
    val department : String,
    val major : String
)

abstract class BaseProgramResponse {
    abstract val children : List<BaseProgramResponse>
    abstract val type : NameZh?
    abstract val requireInfo : Any?
    abstract val remark : String?
    abstract val reference : Boolean
    abstract val planCourses : List<Any>
}


data class ProgramSearchResponse(val data : ProgramSearchBean)

data class ProgramSearchBean(
    override val children : List<ProgramSearchBean>,
    override val type : NameZh?,
    override val requireInfo : SearchRequireInfo?,
    override val remark : String?,
    override val reference : Boolean,
    override val planCourses : List<PlanCoursesSearch>
) : BaseProgramResponse()

data class SearchRequireInfo(
    val requiredSubModuleNum : Int,
    val requiredCredits : Double,
    val requiredCourseNum : Int
)

data class ProgramResponse(override val children : List<ProgramResponse>,
                           override val type : NameZh?,
                           override val requireInfo : RequireInfo?,
                           override val remark : String?,
                           override val reference : Boolean,
                           override val planCourses : List<PlanCourses>,
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
) : BaseProgramResponse()

data class RequireInfo(val requiredSubModuleNum : Int?,
                       val requiredCourseNum : Int?,
                       val totalTheoryPeriods : Int?,
                       val totalTheoryCredits : Double?,
                       val requiredCredits : Double?,
                       val totalPracticePeriods : Double?,
                       val totalPracticeCredits : Double?,
                       val requiredTotalPeriods : Int?,)

data class PlanCourses(val readableTerms : List<Int>,
                       override val compulsory : Boolean,
                       val readableSuggestTerms : List<String>,
                       override val remark : String?,
                       override val course : course,
                       override val openDepartment : NameZh
) : BasePlanCourse()

data class courseForSearch(val nameZh : String,val credits : Double?,val code : String,val courseType : NameZh)

data class PlanCoursesSearch(
                       val terms : List<String>,
                       override val compulsory : Boolean,
                       override val remark : String?,
                       override val course : courseForSearch,
                       val periodInfo : PeriodInfo,
                       override val openDepartment : NameZh
) : BasePlanCourse()

abstract class BasePlanCourse {
    abstract val compulsory: Boolean
    abstract val remark: String?
    abstract val course: Any
    abstract val openDepartment: NameZh
}
//readableTerms开课学期，readableSuggestTerms建议修读学期

data class ProgramPartThree(val term : Int?,
                            val name : String,
                            val credit : Double?,
                            val depart :String,
                            val code : String,
                            val week : Int?,
                            val courseType : String,
                            val remark : String?,
                            val isCompulsory : Boolean
)


data class ProgramCompletionResponse(val total : item,val other : List<item>)
data class item(val name : String,val actual : Double,val full : Double)


data class ProgramBean(
    val completionSummary : Summary ,//已完成
    val outerCompletionSummary : Summary, //培养方案外
    val moduleList : List<ProgramModule>,
    val outerCourseList : List<CourseItem> //term为[]
)

data class Summary(
    val passedCourseNum : Int,
    val failedCourseNum : Int,
    val takingCourseNum : Int,
    val passedCredits : Double,
    val failedCredits : Double,
    val takingCredits : Double
)

data class ProgramModule(
    val nameZh : String,
    val requireInfo : RequireInfo2,
    val completionSummary : Summary,
    val allCourseList : List<CourseItem>,
)
data class RequireInfo2(
    val credits : Double,
    val courseNum : Int
)

data class CourseItem(
    val code : String,
    val nameZh : String,
    val credits : Double,
    val terms : List<String>,
    val resultType : String, //PASSED/TAKING/UNREPAIRED
    val score : Double?, //均分
    val rank : String?, //合格/及格
    val gp : Double? //GPA
)

enum class ProgramCompetitionType(val description: String,val icon : Int) {
    PASSED("已修",R.drawable.star_filled),TAKING("在修",R.drawable.star_half),UNREPAIRED("未修",R.drawable.star),FAILED("挂科",R.drawable.star)
}
fun getProgramCompetitionType(description : String)  : ProgramCompetitionType? = ProgramCompetitionType.entries.find { it.name == description }





package com.hfut.schedule.network.api.model.response.json.jxglstu.program

import com.hfut.schedule.network.api.model.response.json.shared.ProgramBasePlanCourse
import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLessonCourseInfo
import com.hfut.schedule.network.api.model.response.json.shared.ProgramBaseResponse

data class JxglstuProgramResponse(
    override val children : List<JxglstuProgramResponse>,
    override val type : MultiLanguageBaseData?,
    override val requireInfo : JxglstuProgramRequireInfo?,
    override val remark : String?,
    override val reference : Boolean,
    override val planCourses : List<JxglstuProgramPlanCourse>,
    val id : Long,
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
) : ProgramBaseResponse()

data class JxglstuProgramRequireInfo(
    val requiredCredits : Double?
)

data class JxglstuProgramPlanCourse(
    val readableTerms : List<Int>,
    override val compulsory : Boolean,
    val readableSuggestTerms : List<String>,
    override val remark : String?,
    override val course : JxglstuLessonCourseInfo,
    override val openDepartment : MultiLanguageBaseData
) : ProgramBasePlanCourse()








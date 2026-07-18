package com.hfut.schedule.network.api.model.response.json.shared

import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLessonCoursePeriodInfo
import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData

abstract class ProgramBasePlanCourse {
    abstract val compulsory: Boolean
    abstract val remark: String?
    abstract val course: Any
    abstract val openDepartment: MultiLanguageBaseData
}

data class ProgramSearchPlanCourse(
    val terms : List<String>,
    override val compulsory : Boolean,
    override val remark : String?,
    override val course : ProgramSearchPlanCourseInfo,
    val periodInfo : JxglstuLessonCoursePeriodInfo,
    override val openDepartment : MultiLanguageBaseData
) : ProgramBasePlanCourse()

data class ProgramSearchPlanCourseInfo(
    val nameZh : String,
    val credits : Double?,
    val code : String,
    val courseType : MultiLanguageBaseData
)


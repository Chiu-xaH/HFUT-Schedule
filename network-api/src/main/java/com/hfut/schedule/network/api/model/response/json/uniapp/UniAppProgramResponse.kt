package com.hfut.schedule.network.api.model.response.json.uniapp

import com.hfut.schedule.network.api.model.response.json.shared.MultiLanguageBaseData
import com.hfut.schedule.network.api.model.response.json.shared.ProgramBaseResponse
import com.hfut.schedule.network.api.model.response.json.shared.ProgramSearchPlanCourse

data class UniAppProgramResponse(
    val data : UniAppProgramData
)

data class UniAppProgramData(
    override val children : List<UniAppProgramData>,
    override val type : MultiLanguageBaseData?,
    override val requireInfo : UniAppProgramRequireInfo?,
    override val remark : String?,
    override val reference : Boolean,
    override val planCourses : List<ProgramSearchPlanCourse>
) : ProgramBaseResponse()

data class UniAppProgramRequireInfo(
    val requiredSubModuleNum : Int,
    val requiredCredits : Double,
    val requiredCourseNum : Int
)
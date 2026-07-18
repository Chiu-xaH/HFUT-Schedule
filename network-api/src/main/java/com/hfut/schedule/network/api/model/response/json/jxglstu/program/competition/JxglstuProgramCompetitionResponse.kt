package com.hfut.schedule.network.api.model.response.json.jxglstu.program.competition

data class JxglstuProgramCompetitionResponse(
    val completionSummary : JxglstuProgramCompetitionSummary,//已完成
    val outerCompletionSummary : JxglstuProgramCompetitionSummary, //培养方案外
    val moduleList : List<JxglstuProgramCompetitionModule>,
    val outerCourseList : List<JxglstuProgramCompetitionCourse> //term为[]
)

sealed class JxglstuProgramCompetitionDetail {
    data class Outer(val list : List<JxglstuProgramCompetitionCourse>) : JxglstuProgramCompetitionDetail()
    data class Inner(val bean : JxglstuProgramCompetitionModule) : JxglstuProgramCompetitionDetail()
}

data class JxglstuProgramCompetitionSummary(
    val passedCourseNum : Int,
    val failedCourseNum : Int,
    val takingCourseNum : Int,
    val passedCredits : Double,
    val failedCredits : Double,
    val takingCredits : Double,
    val skipCredits : Double
)

data class JxglstuProgramCompetitionModule(
    val moduleId : Long,
    val nameZh : String,
    val requireInfo : JxglstuProgramCompetitionRequireInfo,
    val completionSummary : JxglstuProgramCompetitionSummary,
    val allCourseList : List<JxglstuProgramCompetitionCourse>,
    val allModuleList : List<JxglstuProgramCompetitionModule>
)

data class JxglstuProgramCompetitionRequireInfo(
    val credits : Double,
    val courseNum : Int
)

data class JxglstuProgramCompetitionCourse(
    val code : String,
    val nameZh : String,
    val credits : Double,
    val terms : List<String>,
    val compulsory : Boolean,
    val resultType : String, //PASSED/TAKING/UNREPAIRED
    val score : Double?, //均分
    val rank : String?, //合格/及格
    val gp : Double? //GPA
)


enum class JxglstuProgramCompetitionType(val description: String) {
    PASSED("已修"),
    TAKING("在修"),
    UNREPAIRED("未修"),
    FAILED("挂科")
}


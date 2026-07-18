package com.hfut.schedule.network.api.model.response.json.jxglstu.program.competition

data class JxglstuProgramSimpleCompletionResponse(
    val total : JxglstuProgramSimpleCompletion,
    val other : List<JxglstuProgramSimpleCompletion>
)

data class JxglstuProgramSimpleCompletion(
    val name : String,
    val actual : Double,
    val full : Double
)

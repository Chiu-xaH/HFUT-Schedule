package com.hfut.schedule.network.api.model.response.json.jxglstu

data class JxglstuSelectedCourseConfirmationResponse(
    val detailModels : Map<String, List<JxglstuSelectedCourseConfirmation>>
)

data class JxglstuSelectedCourseConfirmation(
    val courseName : String,
    val courseCode : String,
    val credits : Double
)

enum class JxglstuSelectedCourseConfirmationType(
    val desc : String
) {
    CURRENT_SEMESTER("本学期漏选课程"),
    HISTORY_SEMESTER("历史学期漏选课程"),
    NAVER_NOT_PASSED("未通过课程")
}
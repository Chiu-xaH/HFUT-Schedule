package com.hfut.schedule.network.api.model.response.json.community

data class CommunityExamResponse(
    val result : CommunityExam
)

data class CommunityExam(
    val examArrangementList: List<CommunityExamArrangement>
)

data class CommunityExamArrangement(
    val courseName : String?,
    val place : String?,
    val formatStartTime : String?,
    val formatEndTime : String?
)
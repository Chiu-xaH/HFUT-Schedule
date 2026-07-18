package com.hfut.schedule.network.api.model.response.json.community

data class DormitoryWeeklyScoresDto(
    val semester : String,
    val weeks : List<WeekScore>
)

data class WeekScore(
    val week : Int,
    val scores : List<CommunityDormitoryScore>,
    val total : Double
)

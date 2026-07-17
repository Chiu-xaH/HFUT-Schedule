package com.hfut.schedule.logic.model

import com.hfut.schedule.network.model.response.community.CommunityDormitoryScore

data class DormitoryWeeklyScores(
    val semester : String,
    val weeks : List<WeekScore>
)

data class WeekScore(
    val week : Int,
    val scores : List<CommunityDormitoryScore>,
    val total : Double
)

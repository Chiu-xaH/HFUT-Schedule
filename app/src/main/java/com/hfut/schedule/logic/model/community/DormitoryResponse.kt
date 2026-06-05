package com.hfut.schedule.logic.model.community

data class DormitoryResponse(
    val result : DormitoryBean?
)
data class DormitoryBean(
    val dormitory : String,
    val campus : String,
    val room : String,
//    val campus_dictText : String
)
data class DormitoryInfoResponse(
    val result : DormitoryInfoBean?
)
data class DormitoryInfoBean(
    val profileList: List<Profile>
)

data class Profile(
    val userList: List<DormitoryUser>
)

data class DormitoryUser(
    val username: String,
    val realname: String
)


data class DormitoryScoreResponse(
    val result : List<DormitoryScoreBean>
)

data class DormitoryScoreBean(
    val title : String,
    val value : String,
)

data class DormitoryWeeklyScores(
    val semester : String,
    val weeks : List<WeekScore>
)

data class WeekScore(
    val week : Int,
    val scores : List<DormitoryScoreBean>,
    val total : Double
)


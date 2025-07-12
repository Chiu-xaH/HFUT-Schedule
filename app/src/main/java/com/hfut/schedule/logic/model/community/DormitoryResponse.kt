package com.hfut.schedule.logic.model.community

data class DormitoryResponse(
    val result : List<DormitoryBean>
)
data class DormitoryBean(
    val dormitory : String,
    val campus : String,
    val room : String,
    val campus_dictText : String
)
data class DormitoryInfoResponse(
    val result : DormitoryInfoBean
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

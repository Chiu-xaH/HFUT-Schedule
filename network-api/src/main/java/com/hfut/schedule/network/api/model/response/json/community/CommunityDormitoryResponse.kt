package com.hfut.schedule.network.api.model.response.json.community

import com.google.gson.annotations.SerializedName

data class CommunityDormitoryResponse(
    val result : CommunityDormitory?
)

data class CommunityDormitory(
    val dormitory : String,
    val campus : String,
    val room : String,
)

data class CommunityDormitoryInfoResponse(
    val result : CommunityDormitoryInfo?
)

data class CommunityDormitoryInfo(
    val profileList: List<CommunityDormitoryProfile>
)

data class CommunityDormitoryProfile(
    val userList: List<CommunityDormitoryUser>
)

data class CommunityDormitoryUser(
    @SerializedName("username")
    val studentId: String,
    @SerializedName("realname")
    val name: String
)

data class CommunityDormitoryScoreResponse(
    val result : List<CommunityDormitoryScore>
)

data class CommunityDormitoryScore(
    val title : String,
    val value : String,
)

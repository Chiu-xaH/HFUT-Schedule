package com.hfut.schedule.network.model.response.community

import com.google.gson.annotations.SerializedName

data class CommunityFriendResponse(
    val success : Boolean,
    val result : List<CommunityFriend?>
)

data class CommunityFriend(
    @SerializedName("userId")
    val studentId : String,
    @SerializedName("realname")
    val name : String
)

data class CommunityApplyFriendResponse(
    val success: Boolean,
    val message: String
)

data class CommunityApplyingFriendResponse(
    val result : CommunityApplyingFriendResult
)

data class CommunityApplyingFriendResult(
    val records : List<CommunityApplyingFriendRecord?>
)

data class CommunityApplyingFriendRecord(
    val id : String,
    @SerializedName("applyUserId")
    val studentId : String,
    @SerializedName("applyUsername")
    val name : String
)
package com.hfut.schedule.logic.beans.Community

data class FriendsResopnse(val success : Boolean,val result : List<FriendsList?>)
data class FriendsList(val userId : String,val realname : String)

data class ApplyFriendResponse(val success: Boolean,val message: String)

data class ApplyingResponse(val result : ApplyingList)
data class ApplyingList(val records : List<ApplyingLists?>)
data class ApplyingLists(val id : String,val applyUserId : String,val applyUsername : String)
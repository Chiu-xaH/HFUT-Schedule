package com.hfut.schedule.network.model.response.community

data class CommunityLoginResponse(
    val result : CommunityLoginResult
)

data class CommunityLoginResult(
    val token : String?
)

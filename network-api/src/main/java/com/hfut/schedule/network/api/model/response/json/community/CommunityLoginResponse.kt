package com.hfut.schedule.network.api.model.response.json.community

data class CommunityLoginResponse(
    val result : CommunityLoginResult
)

data class CommunityLoginResult(
    val token : String?
)

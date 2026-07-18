package com.hfut.schedule.network.api.model.response.json.uniapp

sealed class UniAppLoginResponse {
    data class UniAppLoginSuccessResponse(
        val data : UniAppLoginData
    ) : UniAppLoginResponse()
    data class UniAppLoginFailResponse(
        val message : String
    ) : UniAppLoginResponse()
}

data class UniAppLoginData(
    val idToken : String
)


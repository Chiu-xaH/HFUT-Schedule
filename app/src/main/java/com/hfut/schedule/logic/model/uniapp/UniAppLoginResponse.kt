package com.hfut.schedule.logic.model.uniapp

sealed class UniAppLoginResponse {
    data class UniAppLoginSuccessfulResponse(
        val data : UniAppLoginBean
    ) : UniAppLoginResponse()
    data class UniAppLoginError(
        val message : String
    ) : UniAppLoginResponse()
}

data class UniAppLoginBean(
    val idToken : String
)


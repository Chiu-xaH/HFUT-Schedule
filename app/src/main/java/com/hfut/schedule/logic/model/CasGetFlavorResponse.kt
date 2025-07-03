package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class CasGetFlavorResponse(
    @SerializedName("vercode") val needCaptcha : Boolean
)

data class CasGetFlavorBean(
    val needCaptcha : Boolean,
    val jSession : String
)

data class CasCookie(
    val loginFlavoring : String,
    val session : String,
    val jSession : String
) {
//    fun loginCookies() : String = "JSESSIONID=$jSession;SESSION=$session;LOGIN_FLAVORING=$loginFlavoring"
//    fun aesKey() : String = loginFlavoring
//    fun captchaCookies() : String = "JSESSIONID=$jSession"
}


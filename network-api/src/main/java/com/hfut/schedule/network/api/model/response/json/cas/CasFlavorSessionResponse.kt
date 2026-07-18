package com.hfut.schedule.network.api.model.response.json.cas

import com.google.gson.annotations.SerializedName

data class CasFlavorSessionResponse(
    @SerializedName("vercode")
    val needCaptcha : Boolean
)

data class CasGetFlavorSessionDto(
    val needCaptcha : Boolean,
    val jSession : String
)

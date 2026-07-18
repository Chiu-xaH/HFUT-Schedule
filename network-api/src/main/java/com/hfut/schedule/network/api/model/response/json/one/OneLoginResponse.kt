package com.hfut.schedule.network.api.model.response.json.one

import com.google.gson.annotations.SerializedName

data class OneLoginResponse(
    val msg : String,
    val data : OneLoginData
)

data class OneLoginData(
    @SerializedName("access_token")
    val token : String
)

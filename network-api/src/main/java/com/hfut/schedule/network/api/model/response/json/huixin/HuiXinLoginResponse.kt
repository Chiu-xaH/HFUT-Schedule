package com.hfut.schedule.network.api.model.response.json.huixin

import com.google.gson.annotations.SerializedName

data class HuiXinLoginResponse(
    @SerializedName("access_token")
    val token : String
)
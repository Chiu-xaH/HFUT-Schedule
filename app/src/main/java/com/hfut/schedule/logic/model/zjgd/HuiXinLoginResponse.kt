package com.hfut.schedule.logic.model.zjgd

import com.google.gson.annotations.SerializedName

data class HuiXinLoginResponse(
    @SerializedName("access_token")
    val token : String
)

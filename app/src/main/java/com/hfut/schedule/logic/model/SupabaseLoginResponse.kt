package com.hfut.schedule.logic.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

data class SupabaseLoginResponse(
    @SerializedName("access_token") val token : String,
    @SerializedName("refresh_token") val refreshToken : String
)


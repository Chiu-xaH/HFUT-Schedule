package com.hfut.schedule.network.api.model.response.json.supabase

import com.google.gson.annotations.SerializedName

data class SupabaseLoginResponse(
    @SerializedName("access_token")
    val token : String,
    @SerializedName("refresh_token")
    val refreshToken : String
)
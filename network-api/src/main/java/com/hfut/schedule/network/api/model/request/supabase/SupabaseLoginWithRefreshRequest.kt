package com.hfut.schedule.network.api.model.request.supabase

import com.google.gson.annotations.SerializedName

data class SupabaseLoginWithRefreshRequest(
    @SerializedName("refresh_token")
    val refreshToken : String
)
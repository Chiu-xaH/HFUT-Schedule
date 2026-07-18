package com.hfut.schedule.network.api.model.request.supabase

import com.google.gson.annotations.SerializedName

data class SupabaseEventForkCountRequest(
    @SerializedName("target_event_id")
    val eventId : Int
)
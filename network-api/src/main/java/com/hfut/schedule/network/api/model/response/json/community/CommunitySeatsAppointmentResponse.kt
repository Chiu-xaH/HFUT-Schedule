package com.hfut.schedule.network.api.model.response.json.community

import com.google.gson.annotations.SerializedName

data class CommunitySeatsAppointmentResponse(
    val result : CommunitySeatsAppointmentResult
)

data class CommunitySeatsAppointmentResult(
    val records : List<CommunitySeatsAppointment>
)

data class CommunitySeatsAppointment(
    val communityId : String,
    val communityName : String,
    val startTime : String,
    val endTime : String,
    val seatNum : String,
    @SerializedName("campus_dictText")
    val campus : String
)
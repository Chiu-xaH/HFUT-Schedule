package com.hfut.schedule.network.api.model.response.json.community

import com.google.gson.annotations.SerializedName

data class CommunityPlacesAppointmentResponse(
    val result : CommunityPlacesAppointmentResult
)

data class CommunityPlacesAppointmentResult(
    val records : List<CommunityPlacesAppointment>
)

data class CommunityPlacesAppointment(
    val id : String,
    val communityName : String,
    val startTime : String,
    val endTime : String,
    val name : String,
    val logo : String?,
    val address : String,
    @SerializedName("campus_dictText")
    val campus : String,
    val voList : List<CommunityPlacesAppointmentTimeFrame>,
    val adminList : List<CommunityPlacesAppointmentAdmin>
)

data class CommunityPlacesAppointmentTimeFrame(
    val appointmentFlag : String,// 0空闲 1占用
    val timeFrame : String,// HH:MM-HH:MM
    val appointmentId : String?
)

data class CommunityPlacesAppointmentAdmin(
    @SerializedName("username")
    val id : String,
    @SerializedName("realname")
    val name : String,
    val phone : String,
    val telephone : String,
)
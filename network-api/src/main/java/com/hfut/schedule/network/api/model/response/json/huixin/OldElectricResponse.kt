package com.hfut.schedule.network.api.model.response.json.huixin

import com.google.gson.annotations.SerializedName

data class OldElectricResponse(
    @SerializedName("query_elec_roominfo")
    val roomInfo : OldElectricRoomInfo
)

data class OldElectricRoomInfo(
    @SerializedName("errmsg")
    val msg : String
)

package com.hfut.schedule.network.api.model.response.json.hall

import com.google.gson.annotations.SerializedName

data class OfficeHallSearchResponse(
    val data : OfficeHallSearchData
)

data class OfficeHallSearchData(
    val records : List<OfficeHallSearchRecord>
)

data class OfficeHallSearchRecord(
    @SerializedName("lightappId")
    val id : String,
    val name : String,
    val photoUrl : String,
    val serviceDpt : String,
    val serviceTime : String?,
    val processingPlace : String?,
    val url : String?,
    val serviceMode : String
)
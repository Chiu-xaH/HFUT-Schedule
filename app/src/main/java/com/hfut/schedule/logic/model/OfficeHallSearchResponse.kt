package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class OfficeHallSearchResponse(
    val data : OfficeHallSearchData
)
data class OfficeHallSearchData(
    val records : List<OfficeHallSearchBean>
)
data class OfficeHallSearchBean(
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
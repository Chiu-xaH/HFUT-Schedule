package com.hfut.schedule.network.model.response.community

import com.google.gson.annotations.SerializedName

data class CommunityLibraryResponse(
    val result : CommunityLibrary
)

data class CommunityLibrary(
    val records : List<CommunityLibraryRecord>
)

data class CommunityLibraryRecord(
    val callNumber : String,
    val name : String,
    val author : String?,
    val publisher : String?,
    val year : String?,
)

data class CommunityBookPositionResponse(
    val result : List<CommunityBookPosition>
)

data class CommunityBookPosition(
    val place : String,
    @SerializedName("status_dictText")
    val status : String
)


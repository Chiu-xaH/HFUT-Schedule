package com.hfut.schedule.network.model.response.community

import com.google.gson.annotations.SerializedName

data class CommunitySchoolMapResponse(
    val result : List<CommunitySchoolMap>
)

data class CommunitySchoolMap(
    val name : String,
    @SerializedName("currentMap")
    val imageUrl : String
)
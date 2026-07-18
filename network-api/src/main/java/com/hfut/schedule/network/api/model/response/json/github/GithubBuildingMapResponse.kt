package com.hfut.schedule.network.api.model.response.json.github

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.network.api.model.response.json.one.OneBuilding

data class GithubBuildingMapResponse(
    val building : OneBuilding,
    val campus : String,
    val detail : List<GithubBuildingMapFloor>,
)

data class GithubBuildingMapFloor(
    @SerializedName("xml_url")
    val xmlUrl : String,
    @SerializedName("image_url")
    val imageUrl : String,
    val floor : Int
)
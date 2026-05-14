package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.model.one.BuildingBean

data class BuildingMapResponseBean(
    val building : BuildingBean,
    val campus : String,
    val detail : List<BuildingMapFloorBean>,
)

data class BuildingMapFloorBean(
    @SerializedName("xml_url")
    val xmlUrl : String,
    @SerializedName("image_url")
    val imageUrl : String,
    val floor : Int
)
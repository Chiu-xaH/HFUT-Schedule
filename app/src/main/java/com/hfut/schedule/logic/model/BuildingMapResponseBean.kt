package com.hfut.schedule.logic.model

import com.hfut.schedule.logic.model.one.BuildingBean
import kotlinx.serialization.SerialName

data class BuildingMapResponseBean(
    val building : BuildingBean,
    val campus : String,
    val detail : List<BuildingMapFloorBean>,
)

data class BuildingMapFloorBean(
    @SerialName("xml_url")
    val xmlUrl : String,
    @SerialName("image_url")
    val imageUrl : String,
    val floor : Int
)
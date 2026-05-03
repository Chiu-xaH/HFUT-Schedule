package com.hfut.schedule.logic.model

import com.hfut.schedule.logic.model.one.BuildingBean

data class BuildingMapResponseBean(
    val building : BuildingBean,
    val campus : String,
    val detail : List<BuildingMapFloorBean>,
)

data class BuildingMapFloorBean(
    val xmlUrl : String,
    val imageUrl : String,
    val floor : Int
)
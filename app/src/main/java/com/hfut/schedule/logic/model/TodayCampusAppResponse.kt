package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName

data class TodayCampusAppResponse(
    @SerializedName("datas")
    val list : List<TodayCampusApp>
)

data class TodayCampusApp(
    val categoryName : String,
    val apps : List<TodayCampusAppDetail>
)

data class TodayCampusAppDetail(
    val name : String,
    val iconUrl : String,
    val openUrl : String
)
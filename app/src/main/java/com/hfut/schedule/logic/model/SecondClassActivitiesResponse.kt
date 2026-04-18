package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.enumeration.CampusRegion

data class SecondClassActivitiesResponse(
    val list : List<SecondClassActivity>,
    val code : String
)

data class SecondClassActivity(
    val id : String,// scReports/activity/item_detail/$id
    val name : String,
    val module : String,
    // 主办单位
    val sponsor : String,
    val peopleNum : Int,
    val beginTime : String,
    val endTime : String,
    val activePhoto : String,
    // 形式
    val form : String,
    // 2宣 1肥
    @SerializedName("campus")
    val campusCode : Int,
    val keynoteSpeaker : String?,
    val theVenue : String?,
    val lectureStartTime : String?,
    val lectureEndTime : String?
) {
    fun getCampus() : CampusRegion = when(campusCode) {
        2 -> CampusRegion.XUANCHENG
        1 -> CampusRegion.HEFEI
        else -> CampusRegion.HEFEI
    }
}
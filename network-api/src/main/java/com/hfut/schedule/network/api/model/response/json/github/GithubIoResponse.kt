package com.hfut.schedule.network.api.model.response.json.github

import com.google.gson.annotations.SerializedName

data class GithubIoResponse(
    @SerializedName("SettingsInfo")
    val apiInfo : GithubIoApiInfo,
    @SerializedName("Lessons")
    val schedules : GithubIoSchedules,
    @SerializedName("semesterId")
    val semester : String,
    @SerializedName("TimeStamp")
    val focusBottomTip : String,
    @SerializedName("Labs")
    val labs : List<GithubIoLab>,
    @SerializedName("Notifications")
    val notifications : List<GithubIoNotification>,
    @SerializedName("SchoolCalendar")
    val schoolCalendarUrl : String,
    @SerializedName("startDay")
    val termStartDate : String
)

data class GithubIoApiInfo(
    val title : String,
    val info : String,
    val show : Boolean,
    val celebration : Boolean
)

data class GithubIoSchedules(
    @SerializedName("MyList")
    val ddl : List<GithubIoSchedule>,
    @SerializedName("Schedule")
    val schedule : List<GithubIoSchedule>
)

data class GithubIoSchedule(
    val time : String,
    val title : String,
    val info: String,
    val startTime : List<Int>,
    val endTime : List<Int>,
    val showPublic : Boolean
)

data class GithubIoNotification(
    val title : String,
    val info : String,
    val remark : String,
    val url : String?,
    val id : Int
)

data class GithubIoLab(
    val title : String,
    val info : String,
    val type : String
)
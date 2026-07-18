package com.hfut.schedule.logic.model.supabase

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo

data class SupabaseUserTrackRequest(
    @SerializedName("user_name")
    val username : String = getPersonInfo().getNameFinally(),
    @SerializedName("system_version")
    val systemVersion : Int = AppVersion.sdkInt,
    @SerializedName("student_id")
    val studentID : String = getPersonInfo().getStudentIdFinally() ?: "空",
    @SerializedName("campus")
    val campus : String = getPersonInfo().campus ?: "空",
    @SerializedName("department")
    val department : String = getPersonInfo().department ?: "空",
    @SerializedName("app_version_name")
    val appVersionName : String = AppVersion.getVersionName(),
    @SerializedName("app_version_code")
    val appVersionCode : Int = AppVersion.getVersionCode(),
    @SerializedName("device_name")
    val deviceName : String = AppVersion.deviceName
)
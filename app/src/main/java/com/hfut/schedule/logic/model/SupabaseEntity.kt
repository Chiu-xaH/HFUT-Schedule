package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.DateTime
import com.hfut.schedule.logic.util.sys.DateTimeBean
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.EventCampus
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail

data class SupabaseUserLoginBean(
    val email : String? = getSchoolEmail(),
    val password : String
)
data class SupabaseRefreshLoginBean(
    val refresh_token : String
)
data class SupabaseEventForkCount(
    @SerializedName("target_event_id")
    val eventId : Int
)


// 实体类 接收数据库
data class SupabaseEventEntity(
    val id : Int? = null,
    val name : String,
    val description : String?,
    @SerializedName("time_description")
    val timeDescription : String,
    @SerializedName("start_time")
    val startTime : String,
    @SerializedName("end_time")
    val endTime : String,
    @SerializedName("contributor_email")
    val email : String? = getSchoolEmail(),
    @SerializedName("contributor_class")
    val myClass : String? = getPersonInfo().className + when(getCampusRegion()) {
        CampusRegion.HEFEI -> "(肥)"
        CampusRegion.XUANCHENG -> "(宣)"
    },
    @SerializedName("applicable_classes")
    val applicableClasses : String,
    @SerializedName("created_time")
    val createTime: String? = null,
    val url : String? = null,
    val campus : String,
    val type : String
)
// 发送类
data class SupabaseEventOutput(
    val name : String,
    val type : CustomEventType,
    val description : String?,
    val timeDescription : String,
    val dateTime : DateTime,
    val applicableClasses : List<String>,
    val campus : EventCampus,
    val url : String?
)

// 接受类
data class SupabaseEventsInput(
    val id : Int,
    val name : String,
    val type : CustomEventType,
    val description : String?,
    val timeDescription : String,
    val dateTime : DateTime,
    val applicableClasses : List<String>,
    val contributorId : String,
    val contributorClass : String,
    val campus : EventCampus,
    val url : String?,
    val createTime : DateTimeBean
)

// 实体类 接收数据库
data class SupabaseEventForkEntity(
    val id : Int? = null,
    @SerializedName("event_id")
    val eventId : Int,
    @SerializedName("user_email")
    val email : String? = getSchoolEmail(),
    @SerializedName("created_time")
    val createTime: String? = null,
)
// 发送类
data class SupabaseEventForkOutput(
    val eventId : Int
)


data class SupabaseUsageEntity(
    @SerializedName("user_name") val username : String = getPersonInfo().name ?: "游客",
    @SerializedName("system_version") val systemVersion : Int = AppVersion.sdkInt,
    @SerializedName("student_id") val studentID : String = getPersonInfo().studentId ?: "空",
    @SerializedName("campus") val campus : String = getPersonInfo().campus ?: "空",
    @SerializedName("department") val department : String = getPersonInfo().department ?: "空",
    @SerializedName("app_version_name") val appVersionName : String = AppVersion.getVersionName(),
    @SerializedName("app_version_code") val appVersionCode : Int = AppVersion.getVersionCode(),
    @SerializedName("device_name") val deviceName : String = AppVersion.deviceName
)
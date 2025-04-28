package com.hfut.schedule.logic.model

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.util.network.toDateTimeBeanForSupabase
import com.hfut.schedule.logic.util.sys.DateTime
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.EventCampus
import com.hfut.schedule.ui.screen.home.search.function.transfer.getCampus
import com.hfut.schedule.ui.screen.home.search.function.transfer.getEventCampus
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
    val myClass : String? = getPersonInfo().classes + when(getCampus()) {
        Campus.HEFEI -> "(肥)"
        Campus.XUANCHENG -> "(宣)"
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
    val createTime : String
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
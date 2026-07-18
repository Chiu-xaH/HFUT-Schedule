package com.hfut.schedule.logic.model.supabase

import com.google.gson.annotations.SerializedName
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.hfut.schedule.logic.util.sys.DateTime
import com.hfut.schedule.logic.util.sys.DateTimeBean
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.EventCampus
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import com.xah.common.logic.model.CampusRegion

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

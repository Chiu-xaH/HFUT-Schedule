package com.hfut.schedule.logic.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfut.schedule.logic.util.sys.DateTime
import com.hfut.schedule.logic.util.sys.DateTimeBean
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import java.util.Date

@Entity("event")
data class CustomEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val title: String,
    @ColumnInfo("start_time")
    val startTime : Date,
    @ColumnInfo("end_time")
    val endTime : Date,
    val type : String,
    val remark : String,
    val description: String?,
//    @ColumnInfo(name = "create_time", defaultValue = "CURRENT_TIMESTAMP")
//    val createTime : Date = Date(),
    @ColumnInfo(name = "supabase_id")
    val supabaseId : Int? = null
)

enum class CustomEventType {
    NET_COURSE,SCHEDULE,
}

data class CustomEventDTO(
    val id : Int = 0,
    val title: String,
    val dateTime: DateTime,
    val type : CustomEventType,
    val remark : String,
    val description: String?,
//    val createTime: DateTimeBean = DateTimeUtils.DateTimeBeanNow,
    val supabaseId : Int? = null
)
package com.hfut.schedule.logic.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hfut.schedule.logic.utils.DateTime
import java.util.Date

@Entity("event")
data class CustomEventEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val title: String,
    @ColumnInfo("start_time") val startTime : Date,
    @ColumnInfo("end_time") val endTime : Date,
    val type : String,
    val remark : String,
    val description: String?
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
    val description: String?
)
package com.hfut.schedule.logic.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "special_work_day")
data class SpecialWorkDayEntity(
    @PrimaryKey
    @ColumnInfo(name = "origin_date") val originDate: String,
    @ColumnInfo(name = "target_date") val targetDate: String,
)

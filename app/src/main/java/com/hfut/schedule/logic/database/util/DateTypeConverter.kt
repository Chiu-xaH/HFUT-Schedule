package com.hfut.schedule.logic.database.util

import androidx.room.TypeConverter
import java.util.Date

object DateTypeConverter {
    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): Long? = date?.time

    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }
}
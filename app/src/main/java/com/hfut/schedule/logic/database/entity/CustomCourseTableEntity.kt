package com.hfut.schedule.logic.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( "course")
data class CustomCourseTableEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val title : String,
    @ColumnInfo("content_json") val contentJson : String
)

data class CustomCourseTableSummary(
    val id : Int,
    val title : String,
)

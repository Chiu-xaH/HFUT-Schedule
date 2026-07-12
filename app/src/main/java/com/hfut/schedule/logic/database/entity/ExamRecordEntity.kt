package com.hfut.schedule.logic.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exam_record",
    indices = [
        Index(value = ["studentId", "semester", "source", "name", "dateTime"], unique = true),
        Index(value = ["studentId", "semester"])
    ]
)
data class ExamRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dateTime: String,
    val place: String?,
    val type: String?,
    val source: String,
    val studentId: String,
    val semester: Int,
    val fetchedAt: Long
)

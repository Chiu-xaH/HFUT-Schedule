package com.hfut.schedule.logic.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "electric_balance_record",
    indices = [
        Index(value = ["meterKey", "sampledAt"])
    ]
)
data class ElectricBalanceRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val meterKey: String,
    val campusRegion: String,
    val roomName: String,
    val remainingBalance: Double,
    val sampledAt: Long
)

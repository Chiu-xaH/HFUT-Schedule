package com.hfut.schedule.logic.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("shower_label")
data class ShowerLabelEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val mac : String,
    val name : String
)
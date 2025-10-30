package com.hfut.schedule.logic.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("friend")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val studentId : String,
    val name : String,
    val major : String?
)

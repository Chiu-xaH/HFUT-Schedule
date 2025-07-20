package com.hfut.schedule.logic.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "web_url",
    indices = [Index(value = ["url"], unique = true)]
)
data class WebURLEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val url : String,
    val name : String,
    val type : String,
    @ColumnInfo(name = "supabase_id")
    val supabaseId : Int? = null
)
// 来源
enum class WebURLType {
    // 手动添加
    ADDED,
    // 收藏
    COLLECTION,
    // 从 Supabase 下载
    SUPABASE
}

data class WebUrlDTO(
    val url : String,
    val name : String,
    val type : WebURLType,
    val supabaseId : Int? = null
) {
    fun toEntity() : WebURLEntity {
        return WebURLEntity(
            url = url,
            name = name,
            type = type.name,
            supabaseId = supabaseId
        )
    }
}
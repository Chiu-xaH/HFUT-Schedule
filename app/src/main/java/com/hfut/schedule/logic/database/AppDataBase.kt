package com.hfut.schedule.logic.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hfut.schedule.logic.database.dao.CustomCourseTableDao
import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.entity.CustomCourseTableEntity
import com.hfut.schedule.logic.database.entity.CustomEventEntity
import com.hfut.schedule.logic.database.entity.ShowerLabelEntity
import com.hfut.schedule.logic.database.util.DateTypeConverter

// 升级数据库
private const val VERSION = 2

@Database(entities = [CustomEventEntity::class, ShowerLabelEntity::class, CustomCourseTableEntity::class], version = VERSION)
@TypeConverters(DateTypeConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun customEventDao() : CustomEventDao
    abstract fun showerLabelDao() : ShowerLabelDao
    abstract fun customCourseTableDao() : CustomCourseTableDao
}
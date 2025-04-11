package com.hfut.schedule.logic.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfut.schedule.logic.database.dao.CustomCourseTableDao
import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.entity.CustomCourseTableEntity
import com.hfut.schedule.logic.database.entity.CustomEventEntity
import com.hfut.schedule.logic.database.entity.ShowerLabelEntity
import com.hfut.schedule.logic.database.util.DateTypeConverter

@Database(entities = [CustomEventEntity::class, ShowerLabelEntity::class, CustomCourseTableEntity::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun customEventDao() : CustomEventDao
    abstract fun showerLabelDao() : ShowerLabelDao
    abstract fun customCourseTableDao() : CustomCourseTableDao
}
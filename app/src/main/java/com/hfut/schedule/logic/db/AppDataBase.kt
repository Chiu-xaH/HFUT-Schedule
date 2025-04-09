package com.hfut.schedule.logic.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfut.schedule.logic.db.dao.CustomCourseTableDao
import com.hfut.schedule.logic.db.dao.CustomEventDao
import com.hfut.schedule.logic.db.dao.ShowerLabelDao
import com.hfut.schedule.logic.db.entity.CustomCourseTableEntity
import com.hfut.schedule.logic.db.entity.CustomEventEntity
import com.hfut.schedule.logic.db.entity.ShowerLabelEntity
import com.hfut.schedule.logic.db.util.DateTypeConverter

@Database(entities = [CustomEventEntity::class, ShowerLabelEntity::class, CustomCourseTableEntity::class], version = 1)
@TypeConverters(DateTypeConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun customEventDao() : CustomEventDao
    abstract fun showerLabelDao() : ShowerLabelDao
    abstract fun customCourseTableDao() : CustomCourseTableDao
}
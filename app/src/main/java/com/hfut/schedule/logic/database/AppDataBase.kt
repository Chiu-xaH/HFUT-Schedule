package com.hfut.schedule.logic.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfut.schedule.logic.database.dao.CustomCourseTableDao
import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.dao.SpecialWorkDayDao
import com.hfut.schedule.logic.database.dao.WebURLDao
import com.hfut.schedule.logic.database.entity.CustomCourseTableEntity
import com.hfut.schedule.logic.database.entity.CustomEventEntity
import com.hfut.schedule.logic.database.entity.ShowerLabelEntity
import com.hfut.schedule.logic.database.entity.SpecialWorkDayEntity
import com.hfut.schedule.logic.database.entity.WebURLEntity
import com.hfut.schedule.logic.database.util.DateTypeConverter

// 每次记得升级数据库
private const val VERSION = 4

@Database(
    entities = [
        CustomEventEntity::class,
        ShowerLabelEntity::class,
        CustomCourseTableEntity::class,
        SpecialWorkDayEntity::class,
        WebURLEntity::class
               ],
    version = VERSION
)
@TypeConverters(DateTypeConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun customEventDao() : CustomEventDao
    abstract fun showerLabelDao() : ShowerLabelDao
    abstract fun customCourseTableDao() : CustomCourseTableDao
    abstract fun specialWorkDayDao() : SpecialWorkDayDao
    abstract fun webUrlDao() : WebURLDao
}
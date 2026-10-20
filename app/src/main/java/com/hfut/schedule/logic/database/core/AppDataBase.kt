package com.hfut.schedule.logic.database.core

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ExamRecordDao
import com.hfut.schedule.logic.database.dao.FriendDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.dao.SpecialWorkDayDao
import com.hfut.schedule.logic.database.dao.WebURLDao
import com.hfut.schedule.logic.database.entity.CustomEventEntity
import com.hfut.schedule.logic.database.entity.ExamRecordEntity
import com.hfut.schedule.logic.database.entity.FriendEntity
import com.hfut.schedule.logic.database.entity.ShowerLabelEntity
import com.hfut.schedule.logic.database.entity.SpecialWorkDayEntity
import com.hfut.schedule.logic.database.entity.WebURLEntity
import com.hfut.schedule.logic.database.util.DateTypeConverter

// 每次记得升级数据库
private const val VERSION = 7

@Database(
    entities = [
        CustomEventEntity::class,
        ShowerLabelEntity::class,
        SpecialWorkDayEntity::class,
        WebURLEntity::class,
        FriendEntity::class,
        ExamRecordEntity::class
    ],
    version = VERSION
)
@TypeConverters(DateTypeConverter::class)
abstract class AppDataBase : RoomDatabase(), AppDataBaseInf {
    abstract override fun customEventDao() : CustomEventDao
    abstract override fun showerLabelDao() : ShowerLabelDao
    abstract override fun specialWorkDayDao() : SpecialWorkDayDao
    abstract override fun webUrlDao() : WebURLDao
    abstract override fun friendDao() : FriendDao
    abstract override fun examRecordDao() : ExamRecordDao
}

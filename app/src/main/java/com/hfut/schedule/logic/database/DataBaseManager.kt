package com.hfut.schedule.logic.database

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.database.dao.CustomCourseTableDao
import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao

// 版本1到版本2
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE event ADD COLUMN supabase_id INTEGER DEFAULT NULL")
    }
}

object DataBaseManager {
    private val db: AppDataBase by lazy {
        Room.databaseBuilder(
            MyApplication.context,
            AppDataBase::class.java,
            "app-database"
        ).addMigrations(MIGRATION_1_2).build()
    }

    val customEventDao: CustomEventDao by lazy { db.customEventDao() }
    val showerLabelDao: ShowerLabelDao by lazy { db.showerLabelDao() }
    val customCourseTableDao: CustomCourseTableDao by lazy { db.customCourseTableDao() }
}

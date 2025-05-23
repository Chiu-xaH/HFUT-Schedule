package com.hfut.schedule.logic.database

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.database.dao.CustomCourseTableDao
import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.dao.SpecialWorkDayDao

// 版本1到版本2
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE event ADD COLUMN supabase_id INTEGER DEFAULT NULL")
    }
}
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS special_work_day (
                origin_date TEXT NOT NULL PRIMARY KEY,
                target_date TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}

object DataBaseManager {
    private val db: AppDataBase by lazy {
        Room.databaseBuilder(
            MyApplication.context,
            AppDataBase::class.java,
            "app-database"
        ).addMigrations(MIGRATION_1_2,MIGRATION_2_3).build()
    }

    val customEventDao: CustomEventDao by lazy { db.customEventDao() }
    val showerLabelDao: ShowerLabelDao by lazy { db.showerLabelDao() }
    val customCourseTableDao: CustomCourseTableDao by lazy { db.customCourseTableDao() }
    val specialWorkDayDao: SpecialWorkDayDao by lazy { db.specialWorkDayDao() }
}

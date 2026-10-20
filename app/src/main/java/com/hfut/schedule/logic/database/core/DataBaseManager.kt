package com.hfut.schedule.logic.database.core

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

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

private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS web_url (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                url TEXT NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                supabase_id INTEGER
            )
            """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX index_web_url_url ON web_url(url)")
    }
}

private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 新建表friend
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `friend` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `studentId` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `major` TEXT
            )
            """.trimIndent()
        )
        // 删除表course
        db.execSQL("DROP TABLE IF EXISTS `course`")
    }
}

private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS exam_record (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                dateTime TEXT NOT NULL,
                place TEXT,
                type TEXT,
                source TEXT NOT NULL,
                semester INTEGER NOT NULL,
                fetchedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_exam_record_semester_source_name_dateTime ON exam_record(semester, source, name, dateTime)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_exam_record_semester ON exam_record(semester)"
        )
    }
}

private val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE event ADD COLUMN group_id TEXT")
    }
}

object DataBaseManager {

    private val enableSafeMode by lazy {
        runBlocking {
            !DataStoreManager.enableDataStore.first()
        }
    }

    private val dataBaseSafely by lazy {
        AppDataBaseSafely()
    }
    private val dataBase by lazy {
        Room.databaseBuilder(
            MyApplication.context,
            AppDataBase::class.java,
            "app-database"
        ).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6,
            MIGRATION_6_7
        ).build()
    }

    val customEventDao by lazy {
        if(!enableSafeMode) {
            dataBase.customEventDao()
        } else {
            dataBaseSafely.customEventDao()
        }
    }
    val showerLabelDao by lazy {
        if(!enableSafeMode) {
            dataBase.showerLabelDao()
        } else {
            dataBaseSafely.showerLabelDao()
        }
    }
    val specialWorkDayDao by lazy {
        if(!enableSafeMode) {
            dataBase.specialWorkDayDao()
        } else {
            dataBaseSafely.specialWorkDayDao()
        }
    }
    val webUrlDao by lazy {
        if(!enableSafeMode) {
            dataBase.webUrlDao()
        } else {
            dataBaseSafely.webUrlDao()
        }
    }
    val friendDao by lazy {
        if(!enableSafeMode) {
            dataBase.friendDao()
        } else {
            dataBaseSafely.friendDao()
        }
    }
    val examRecordDao by lazy {
        if(!enableSafeMode) {
            dataBase.examRecordDao()
        } else {
            dataBaseSafely.examRecordDao()
        }
    }
}

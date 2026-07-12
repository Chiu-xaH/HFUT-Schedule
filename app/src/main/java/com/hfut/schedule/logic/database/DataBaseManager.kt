package com.hfut.schedule.logic.database

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ExamRecordDao
import com.hfut.schedule.logic.database.dao.FriendDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.dao.SpecialWorkDayDao
import com.hfut.schedule.logic.database.dao.WebURLDao

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
                studentId TEXT NOT NULL,
                semester INTEGER NOT NULL,
                fetchedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_exam_record_studentId_semester_source_name_dateTime ON exam_record(studentId, semester, source, name, dateTime)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_exam_record_studentId_semester ON exam_record(studentId, semester)"
        )
    }
}

object DataBaseManager {
    private val db: AppDataBase by lazy {
        Room.databaseBuilder(
            MyApplication.context,
            AppDataBase::class.java,
            "app-database"
        ).addMigrations(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5,
            MIGRATION_5_6
        ).build()
    }

    val customEventDao: CustomEventDao by lazy { db.customEventDao() }
    val showerLabelDao: ShowerLabelDao by lazy { db.showerLabelDao() }
    val specialWorkDayDao: SpecialWorkDayDao by lazy { db.specialWorkDayDao() }
    val webUrlDao: WebURLDao by lazy { db.webUrlDao() }
    val friendDao: FriendDao by lazy { db.friendDao() }
    val examRecordDao: ExamRecordDao by lazy { db.examRecordDao() }
}

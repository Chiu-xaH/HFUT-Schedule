package com.hfut.schedule.logic.database

import androidx.room.Room
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.database.dao.CustomCourseTableDao
import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao


object DataBaseManager {
    private val db: AppDataBase by lazy {
        Room.databaseBuilder(
            MyApplication.context,
            AppDataBase::class.java,
            "app-database"
        ).build()
    }

    val customEventDao: CustomEventDao by lazy { db.customEventDao() }
    val showerLabelDao: ShowerLabelDao by lazy { db.showerLabelDao() }
    val customCourseTableDao: CustomCourseTableDao by lazy { db.customCourseTableDao() }
}

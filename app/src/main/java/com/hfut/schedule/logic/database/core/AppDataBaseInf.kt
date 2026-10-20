package com.hfut.schedule.logic.database.core

import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ExamRecordDao
import com.hfut.schedule.logic.database.dao.FriendDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.dao.SpecialWorkDayDao
import com.hfut.schedule.logic.database.dao.WebURLDao

interface AppDataBaseInf {
    fun customEventDao() : CustomEventDao
    fun showerLabelDao() : ShowerLabelDao
    fun specialWorkDayDao() : SpecialWorkDayDao
    fun webUrlDao() : WebURLDao
    fun friendDao() : FriendDao
    fun examRecordDao() : ExamRecordDao
}
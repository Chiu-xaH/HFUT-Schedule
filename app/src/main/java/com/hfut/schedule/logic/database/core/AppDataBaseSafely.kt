package com.hfut.schedule.logic.database.core

import com.hfut.schedule.logic.database.dao.CustomEventDao
import com.hfut.schedule.logic.database.dao.ExamRecordDao
import com.hfut.schedule.logic.database.dao.FriendDao
import com.hfut.schedule.logic.database.dao.ShowerLabelDao
import com.hfut.schedule.logic.database.dao.SpecialWorkDayDao
import com.hfut.schedule.logic.database.dao.WebURLDao
import com.hfut.schedule.logic.database.impl.CustomEventDaoImplSafely
import com.hfut.schedule.logic.database.impl.ExamRecordDaoImplSafely
import com.hfut.schedule.logic.database.impl.FriendDaoImplSafely
import com.hfut.schedule.logic.database.impl.ShowerLabelDaoImplSafely
import com.hfut.schedule.logic.database.impl.SpecialWorkDayDaoImplSafely
import com.hfut.schedule.logic.database.impl.WebURLDaoImplSafely

class AppDataBaseSafely : AppDataBaseInf {
    private val customEventDaoImpl by lazy { CustomEventDaoImplSafely() }
    private val showerLabelDaoImpl by lazy { ShowerLabelDaoImplSafely() }
    private val specialWorkDayDaoImpl by lazy { SpecialWorkDayDaoImplSafely() }
    private val webUrlDaoImpl by lazy { WebURLDaoImplSafely() }
    private val friendDaoImpl by lazy { FriendDaoImplSafely() }
    private val examRecordDaoImpl by lazy { ExamRecordDaoImplSafely() }

    override fun customEventDao(): CustomEventDao = customEventDaoImpl
    override fun showerLabelDao(): ShowerLabelDao = showerLabelDaoImpl
    override fun specialWorkDayDao(): SpecialWorkDayDao = specialWorkDayDaoImpl
    override fun webUrlDao(): WebURLDao = webUrlDaoImpl
    override fun friendDao(): FriendDao = friendDaoImpl
    override fun examRecordDao(): ExamRecordDao = examRecordDaoImpl
}
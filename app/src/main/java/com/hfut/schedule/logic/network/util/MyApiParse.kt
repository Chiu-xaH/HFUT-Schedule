package com.hfut.schedule.logic.network.util

import com.google.gson.Gson
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.logic.model.Lessons
import com.hfut.schedule.logic.model.MyAPIResponse
import com.hfut.schedule.logic.model.Schedule
import com.hfut.schedule.logic.model.SettingsInfo
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.xah.uicommon.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MyApiParse {
    @JvmStatic
    fun getMy() : MyAPIResponse? {
        val json = SharedPrefs.prefs.getString("my","")
        return try {
            Gson().fromJson(json, MyAPIResponse::class.java)
        } catch (e : Exception) {
            LogUtil.error(e)
            null
        }
    }
    @JvmStatic
    fun getAPICelebration() : Boolean {
        return try {
            getSettingInfo().celebration
        } catch (e: Exception) {
            LogUtil.error(e)
            false
        }
    }
    @JvmStatic
    fun getSettingInfo() : SettingsInfo {
        return try {
            getMy()!!.SettingsInfo
        } catch (e: Exception) {
            LogUtil.error(e)
            SettingsInfo(
                title = "开发者接口",
                info = "本接口在不更新APP前提下可实时更新信息",
                show = false,
                celebration = false
            )
        }
    }

    @JvmStatic
    private fun getAPISchedule(): Lessons? {
        return try {
            getMy()!!.Lessons
        } catch (e : Exception) {
            LogUtil.error(e)
            null
        }
    }
    @JvmStatic
    fun getSchedule() : List<Schedule> {
        try {
            val list = getAPISchedule()?.Schedule ?: return emptyList()
            return list
        } catch (e : Exception) {
            LogUtil.error(e)
            return emptyList()
        }
    }
    @JvmStatic
    suspend fun getCustomEvent(isSupabase : Boolean = false) : List<CustomEventDTO> =
        withContext(Dispatchers.IO) {
            val dtoList = mutableListOf<CustomEventDTO>()
            val list =
                if (isSupabase) DataBaseManager.customEventDao.getDownloadedByTime() else DataBaseManager.customEventDao.getAllSortedByTime()
            list.forEach {
                dtoList.add(CustomEventMapper.entityToDto(it))
            }
            return@withContext dtoList
        }

    @JvmStatic
    fun getNetCourse() : List<Schedule> {
        try {
            val list = getAPISchedule()?.MyList ?: return emptyList()
            return list
        } catch (e : Exception) {
            LogUtil.error(e)
            return emptyList()
        }
    }
    @JvmStatic
    fun getTimeStamp() : String? {
        return try {
            getMy()?.TimeStamp
        } catch (e : Exception) {
            LogUtil.error(e)
            null
        }
    }
    @JvmStatic
    fun isNextOpen() : Boolean {
        return try {
            getMy()!!.Next
        } catch (e : Exception) {
            LogUtil.error(e)
            false
        }
    }

}
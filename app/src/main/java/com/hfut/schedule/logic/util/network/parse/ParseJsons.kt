package com.hfut.schedule.logic.util.network.parse

import android.util.Log
import com.google.gson.Gson
import com.hfut.schedule.logic.model.Lessons
import com.hfut.schedule.logic.model.MyAPIResponse
import com.hfut.schedule.logic.model.Schedule
import com.hfut.schedule.logic.model.SettingsInfo
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

object ParseJsons {
    @JvmStatic
    fun getMy() : MyAPIResponse? {
        val json = prefs.getString("my","")
        return try {
            Gson().fromJson(json, MyAPIResponse::class.java)
        } catch (e : Exception) {
            null
        }
    }
    @JvmStatic
    fun getAPICelebration() : Boolean {
        return try {
            getSettingInfo().celebration
        } catch (e: Exception) {
            false
        }
    }
    @JvmStatic
    fun getSettingInfo() : SettingsInfo {
        return try {
            getMy()!!.SettingsInfo
        } catch (e: Exception) {
            SettingsInfo(
                title = "开发者接口",
                info = "本接口在不更新APP前提下可实时更新信息",
                show = false,
                celebration = false
            )
        }
    }
    @JvmStatic
    fun useCaptcha() : Boolean {
        return try {
            getMy()!!.useCaptcha
        } catch (e: Exception) {
            false
        }
    }
    @JvmStatic
    fun getURL() : String {
        return try {
            getMy()!!.API
        } catch (e : Exception) {
            "https://www.chiuxah.xyz/"
        }
    }
    @JvmStatic
    private fun getAPISchedule(): Lessons? {
        return try {
            getMy()!!.Lessons
        } catch (e : Exception) {
            null
        }
    }
    @JvmStatic
    fun getSchedule() : List<Schedule> {
        try {
            val list = getAPISchedule()?.Schedule ?: return emptyList()
            return list
//                .sortedBy {
//                val time = it.startTime
//                LocalDateTime.of(time[0], time[1], time[2], time[3], time[4])
//            }
        } catch (_ : Exception) {
            return emptyList()
        }
    }
    @JvmStatic
    suspend fun getCustomEvent(type: CustomEventType,isSupabase : Boolean = false) : List<CustomEventDTO> = withContext(Dispatchers.IO) {
        val dtoList = mutableListOf<CustomEventDTO>()
        val list = if(isSupabase) DataBaseManager.customEventDao.getDownloaded(type.name) else DataBaseManager.customEventDao.getAll(type.name)
        list.forEach {
            dtoList.add(CustomEventMapper.entityToDto(it))
        }
        return@withContext dtoList
    }
    @JvmStatic
    suspend fun getCustomSchedule(isSupabase : Boolean = false) : List<CustomEventDTO> = getCustomEvent(CustomEventType.SCHEDULE,isSupabase).sortedBy {
        with(it.dateTime.start) {
            LocalDateTime.of(year, month, day, hour, minute)
        }
    }
    @JvmStatic
    suspend fun getCustomNetCourse(isSupabase : Boolean = false) : List<CustomEventDTO> = getCustomEvent(CustomEventType.NET_COURSE,isSupabase).sortedBy {
        with(it.dateTime.end) {
            LocalDateTime.of(year, month, day, hour, minute)
        }
    }
    @JvmStatic
    fun getNetCourse() : List<Schedule> {
        try {
            val list = getAPISchedule()?.MyList ?: return emptyList()
            return list
//                .sortedBy {
//                val time = it.endTime
//                LocalDateTime.of(time[0], time[1], time[2], time[3], time[4])
//            }
        } catch (_ : Exception) {
            return emptyList()
        }
    }
    @JvmStatic
    fun getTimeStamp() : String? {
        return try {
            getMy()?.TimeStamp
        } catch (_ : Exception) {
            null
        }
    }
    @JvmStatic
    fun isNextOpen() : Boolean {
        return try {
            getMy()!!.Next
        } catch (_:Exception) {
            false
        }
    }

}
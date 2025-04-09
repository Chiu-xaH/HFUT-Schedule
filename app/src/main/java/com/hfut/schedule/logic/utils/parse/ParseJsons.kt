package com.hfut.schedule.logic.utils.parse

import android.util.Log
import com.google.gson.Gson
import com.hfut.schedule.logic.beans.Lessons
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.beans.Schedule
import com.hfut.schedule.logic.beans.SettingsInfo
import com.hfut.schedule.logic.db.RoomDataBaseManager
import com.hfut.schedule.logic.db.entity.CustomEventDTO
import com.hfut.schedule.logic.db.entity.CustomEventType
import com.hfut.schedule.logic.db.util.CustomEventMapper
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
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
    private suspend fun getCustomEvent(type: CustomEventType) : List<CustomEventDTO> = withContext(Dispatchers.IO) {
        val dtoList = mutableListOf<CustomEventDTO>()
        val list = RoomDataBaseManager.customEventDao.getAll(type.name)
        list.forEach {
            dtoList.add(CustomEventMapper.entityToDto(it))
        }
        return@withContext dtoList
    }
    @JvmStatic
    suspend fun getCustomSchedule() : List<CustomEventDTO> = getCustomEvent(CustomEventType.SCHEDULE).sortedBy {
        with(it.dateTime.start) {
            LocalDateTime.of(year, month, day, hour, minute)
        }
    }
    @JvmStatic
    suspend fun getCustomNetCourse() : List<CustomEventDTO> = getCustomEvent(CustomEventType.NET_COURSE).sortedBy {
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
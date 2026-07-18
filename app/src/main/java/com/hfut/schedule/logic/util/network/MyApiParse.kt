package com.hfut.schedule.logic.util.network


import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.network.api.model.response.json.github.GithubIoSchedules
import com.hfut.schedule.network.api.model.response.json.github.GithubIoResponse
import com.hfut.schedule.network.api.model.response.json.github.GithubIoSchedule
import com.hfut.schedule.network.api.model.response.json.github.GithubIoApiInfo
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.network.core.GsonInstance
import com.xah.common.logic.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MyApiParse {
    @JvmStatic
    fun getMy() : GithubIoResponse? {
        val json = SharedPrefs.prefs.getString("my","")
        return try {
            GsonInstance.fromJson(json, GithubIoResponse::class.java)
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
    fun getSettingInfo() : GithubIoApiInfo {
        return try {
            getMy()!!.apiInfo
        } catch (e: Exception) {
            LogUtil.error(e)
            GithubIoApiInfo(
                title = "开发者接口",
                info = "本接口在不更新APP前提下可实时更新信息",
                show = false,
                celebration = false
            )
        }
    }

    @JvmStatic
    private fun getAPISchedule(): GithubIoSchedules? {
        return try {
            getMy()!!.schedules
        } catch (e : Exception) {
            LogUtil.error(e)
            null
        }
    }
    @JvmStatic
    fun getSchedule() : List<GithubIoSchedule> {
        try {
            val list = getAPISchedule()?.schedule ?: return emptyList()
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
    fun getNetCourse() : List<GithubIoSchedule> {
        try {
            val list = getAPISchedule()?.ddl ?: return emptyList()
            return list
        } catch (e : Exception) {
            LogUtil.error(e)
            return emptyList()
        }
    }
    @JvmStatic
    fun getTimeStamp() : String? {
        return try {
            getMy()?.focusBottomTip
        } catch (e : Exception) {
            LogUtil.error(e)
            null
        }
    }
}
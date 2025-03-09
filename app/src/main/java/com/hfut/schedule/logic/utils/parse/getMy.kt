package com.hfut.schedule.logic.utils.parse

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.beans.SettingsInfo
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs

fun getMy() : MyAPIResponse? {
    val json = prefs.getString("my","")
    return try {
        Gson().fromJson(json,MyAPIResponse::class.java)
    } catch (e : Exception) {
        null
    }
}

fun getCelebration() : Boolean {
    return try {
        getSettingInfo().celebration
    } catch (e: Exception) {
        false
    }
}

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

fun useCaptcha() : Boolean {
    return try {
        getMy()!!.useCaptcha
    } catch (e: Exception) {
        false
    }
}
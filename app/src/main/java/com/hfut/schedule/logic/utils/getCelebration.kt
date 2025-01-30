package com.hfut.schedule.logic.utils

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.viewmodel.NetWorkViewModel

fun getCelebration() : Boolean {
    val json = prefs.getString("my",MyApplication.NullMy)
    return try {
        Gson().fromJson(json,MyAPIResponse::class.java).SettingsInfo.celebration
    } catch (e: Exception) {
        false
    }
}
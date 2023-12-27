package com.hfut.schedule.ui.ComposeUI.Settings

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.datamodel.MyAPIResponse

fun getMyVersion() : String {
    val my = SharePrefs.prefs.getString("my", MyApplication.NullMy)
    val data = Gson().fromJson(my, MyAPIResponse::class.java).SettingsInfo
    val version = data.version
    SharePrefs.Save("version", version)
    return version
}
package com.hfut.schedule.ui.activity.home.search.functions.notifications

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.beans.Notifications
import com.hfut.schedule.logic.utils.data.SharePrefs


//解析通知
fun getNotifications() : List<Notifications> {
    try {
        val json = SharePrefs.prefs.getString("my", MyApplication.NullMy)
        return Gson().fromJson(json, MyAPIResponse::class.java).Notifications
    } catch (_:Exception) {
        return emptyList()
    }
}
package com.hfut.schedule.ui.activity.home.search.functions.notifications

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.beans.Notifications
import com.hfut.schedule.logic.utils.SharePrefs


//解析通知
fun getNotifications() : MutableList<Notifications> {
    var Notifications = mutableListOf<Notifications>()
    try {
        val json = SharePrefs.prefs.getString("my", MyApplication.NullMy)
        val list = Gson().fromJson(json, MyAPIResponse::class.java)?.Notifications

        list?.forEach { Notifications.add(it) }
        return Notifications
    } catch (_:Exception) {
        return Notifications
    }
}
package com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.datamodel.Notifications
import com.hfut.schedule.logic.utils.SharePrefs


//解析通知
fun getNotifications() : MutableList<Notifications>{
    val json = SharePrefs.prefs.getString("my", MyApplication.NullMy)
    val list = Gson().fromJson(json, MyAPIResponse::class.java)?.Notifications
    var Notifications = mutableListOf<Notifications>()
    list?.forEach { Notifications.add(it) }
    return Notifications
}
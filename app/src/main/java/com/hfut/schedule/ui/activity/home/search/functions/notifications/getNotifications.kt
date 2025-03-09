package com.hfut.schedule.ui.activity.home.search.functions.notifications

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.beans.Notifications
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.parse.getMy


//解析通知
fun getNotifications() : List<Notifications> {
    return try {
        getMy()!!.Notifications
    } catch (_:Exception) {
        emptyList()
    }
}
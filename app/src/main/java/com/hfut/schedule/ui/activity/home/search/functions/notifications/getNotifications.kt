package com.hfut.schedule.ui.activity.home.search.functions.notifications

import com.hfut.schedule.logic.beans.Notifications
import com.hfut.schedule.logic.utils.parse.ParseJsons.getMy


//解析通知
fun getNotifications() : List<Notifications> {
    return try {
        getMy()!!.Notifications
    } catch (_:Exception) {
        emptyList()
    }
}
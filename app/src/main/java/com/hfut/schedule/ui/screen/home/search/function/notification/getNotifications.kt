package com.hfut.schedule.ui.screen.home.search.function.notification

import com.hfut.schedule.logic.model.Notifications
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getMy


//解析通知
fun getNotifications() : List<Notifications> {
    return try {
        getMy()!!.Notifications
    } catch (_:Exception) {
        emptyList()
    }
}
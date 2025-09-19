package com.hfut.schedule.ui.screen.home.search.function.my.notification

import com.hfut.schedule.logic.model.Notifications
import com.hfut.schedule.logic.network.util.MyApiParse.getMy


//解析通知
fun getNotifications() : List<Notifications> {
    return try {
        getMy()!!.Notifications
    } catch (_:Exception) {
        emptyList()
    }
}
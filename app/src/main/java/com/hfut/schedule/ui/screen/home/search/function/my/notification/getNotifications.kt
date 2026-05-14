package com.hfut.schedule.ui.screen.home.search.function.my.notification

import com.hfut.schedule.logic.model.Notifications
import com.hfut.schedule.logic.util.network.MyApiParse.getMy
import com.xah.shared.LogUtil


//解析通知
fun getNotifications() : List<Notifications> {
    return try {
        getMy()!!.Notifications
    } catch (e:Exception) {
        LogUtil.error(e)
        emptyList()
    }
}
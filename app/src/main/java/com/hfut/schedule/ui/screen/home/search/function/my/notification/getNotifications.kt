package com.hfut.schedule.ui.screen.home.search.function.my.notification

import com.hfut.schedule.network.api.model.response.json.github.GithubIoNotification
import com.hfut.schedule.logic.util.network.MyApiParse.getMy
import com.xah.common.logic.util.LogUtil


//解析通知
fun getNotifications() : List<GithubIoNotification> {
    return try {
        getMy()!!.notifications
    } catch (e:Exception) {
        LogUtil.error(e)
        emptyList()
    }
}
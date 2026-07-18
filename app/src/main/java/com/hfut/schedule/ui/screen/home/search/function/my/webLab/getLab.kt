package com.hfut.schedule.ui.screen.home.search.function.my.webLab


import com.hfut.schedule.network.api.model.response.json.github.GithubIoLab
import com.hfut.schedule.logic.util.network.MyApiParse.getMy
import com.xah.common.logic.util.LogUtil

fun getLab() : List<GithubIoLab>{
    return try {
        getMy()!!.labs
    } catch (e : Exception) {
        LogUtil.error(e)
        emptyList()
    }
}
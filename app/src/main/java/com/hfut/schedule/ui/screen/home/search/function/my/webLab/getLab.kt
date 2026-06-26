package com.hfut.schedule.ui.screen.home.search.function.my.webLab


import com.hfut.schedule.logic.model.Lab
import com.hfut.schedule.logic.model.MyAPIResponse
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.network.MyApiParse.getMy
import com.xah.common.logic.util.LogUtil

fun getLab() : List<Lab>{
    return try {
        getMy()!!.Labs
    } catch (e : Exception) {
        LogUtil.error(e)
        emptyList()
    }
}
package com.hfut.schedule.ui.activity.home.focus

import com.hfut.schedule.logic.utils.parse.ParseJsons.getMy

fun getURL() : String {
    return try {
        getMy()!!.API
    } catch (e : Exception) {
        "https://www.chiuxah.xyz/"
    }
}

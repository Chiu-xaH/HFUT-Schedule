package com.hfut.schedule.ui.screen.home.search.function.webLab

import com.google.gson.Gson
import com.hfut.schedule.logic.model.Lab
import com.hfut.schedule.logic.model.MyAPIResponse
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getMy

fun getLab() : List<Lab>{
    return try {
        getMy()!!.Labs
    } catch (e : Exception) {
        emptyList()
    }
}
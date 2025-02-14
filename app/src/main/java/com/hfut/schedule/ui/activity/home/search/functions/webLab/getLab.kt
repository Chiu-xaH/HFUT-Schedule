package com.hfut.schedule.ui.activity.home.search.functions.webLab

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.Lab
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs

fun getLab() : List<Lab>{
    val json = SharePrefs.prefs.getString("my","")
    return try {
        Gson().fromJson(json, MyAPIResponse::class.java).Labs
    } catch (e : Exception) {
        emptyList()
    }
}
package com.hfut.schedule.ui.activity.home.search.functions.webLab

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.Lab
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs

fun getLab() : MutableList<Lab>{
    var Items = mutableListOf<Lab>()
    val json = SharePrefs.prefs.getString("my","")
    return try {
        val item = Gson().fromJson(json, MyAPIResponse::class.java).Labs
        for (i in 0 until item.size) {
            val title = item[i].title
            val info = item[i].info
            val type = item[i].type
            Items.add(Lab(title, info, type))
        }
        Items
    } catch (e : Exception) {
        Items
    }
}
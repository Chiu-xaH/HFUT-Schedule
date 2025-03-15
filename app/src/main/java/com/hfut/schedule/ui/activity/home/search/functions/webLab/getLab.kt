package com.hfut.schedule.ui.activity.home.search.functions.webLab

import com.google.gson.Gson
import com.hfut.schedule.logic.beans.Lab
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.parse.ParseJsons.getMy

fun getLab() : List<Lab>{
    return try {
        getMy()!!.Labs
    } catch (e : Exception) {
        emptyList()
    }
}
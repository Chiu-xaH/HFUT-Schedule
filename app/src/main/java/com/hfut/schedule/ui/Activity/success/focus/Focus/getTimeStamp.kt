package com.hfut.schedule.ui.Activity.success.focus.Focus

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs

fun getTimeStamp() : String? {
    val my = prefs.getString("my", MyApplication.NullMy)
    if (my != null) {
        if(my.contains("更新")) {
            val data = Gson().fromJson(my, MyAPIResponse::class.java).TimeStamp
            return data
        } else return "未获取到"
    } else return "未获取到"
}
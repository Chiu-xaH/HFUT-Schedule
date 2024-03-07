package com.hfut.schedule.ui.Activity.success.search.Search.Program

import android.util.Log
import com.google.gson.Gson
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Jxglstu.ProgramResponse
import com.hfut.schedule.logic.utils.SharePrefs.prefs

fun getProgram()  {
    val json = prefs.getString("program","")
    val result = Gson().fromJson(json,ProgramResponse::class.java)
    val children = result.children
    if (children != null) {
        for (element in children) {
            val remark = element?.remark
            remark?.let { Log.d("备注", it) }
        }
    }
}
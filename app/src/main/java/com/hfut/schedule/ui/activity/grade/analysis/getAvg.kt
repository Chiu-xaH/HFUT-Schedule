package com.hfut.schedule.ui.activity.grade.analysis

import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.community.AvgResult
import com.hfut.schedule.logic.beans.community.GradeAvgResponse
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import java.lang.Exception

fun getAvg(): AvgResult {
    val jsonAvg = prefs.getString("Avg","")
    return try {
         Gson().fromJson(jsonAvg, GradeAvgResponse::class.java).result
    } catch (e : Exception) {
        AvgResult(null,null,null,null,null,null)
    }
}